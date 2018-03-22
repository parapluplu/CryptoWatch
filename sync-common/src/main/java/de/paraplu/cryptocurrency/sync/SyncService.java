package de.paraplu.cryptocurrency.sync;

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.web3j.protocol.Web3j;
import org.web3j.tx.ClientTransactionManager;

import de.paraplu.cryptocurrency.domain.Erc20TokenWrapper;
import de.paraplu.cryptocurrency.domain.Erc20TokenWrapper.TransferEventResponse;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.SyncStatusInfo;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.meta.SyncStatus;
import de.paraplu.cryptocurrency.domain.mongodb.repository.SyncStatusInfoRepository;
import de.paraplu.cryptocurrency.domain.neo4j.pojo.Address;
import de.paraplu.cryptocurrency.domain.neo4j.repository.AddressRepository;
import de.paraplu.cryptocurrency.util.Web3Util;
import rx.functions.Func1;

@Component
public class SyncService {

    private static final Logger      LOGGER = LoggerFactory.getLogger(SyncService.class);

    @Autowired
    private SyncStatusInfoRepository syncStatusInfoRepository;
    @Autowired
    private AddressRepository        addressRepository;

    @Autowired
    private Web3j                    web3;

    @Value(value = "${sync.batchSize}")
    private BigInteger               batchSize;

    @Async
    public void sync(SyncStatusInfo syncStatusInfo) throws SyncServiceException {
        try {
            ClientTransactionManager transactionManager = new ClientTransactionManager(
                    web3,
                    syncStatusInfo.getContractAdress());
            Erc20TokenWrapper erc20TokenWrapper = Erc20TokenWrapper.load(
                    syncStatusInfo.getContractAdress(),
                    web3,
                    transactionManager,
                    BigInteger.ZERO,
                    BigInteger.ZERO);
            StopWatch sw = new StopWatch("past-sync");
            sw.start();
            syncStatusInfo.setStatus(SyncStatus.SYNCING);
            syncStatusInfoRepository.save(syncStatusInfo);
            BigInteger first = syncStatusInfo.getFrom();
            BigInteger last = syncStatusInfo.getTo();
            final AtomicBoolean successFlag = new AtomicBoolean(true);
            for (BigInteger from = first; from.compareTo(last) == -1; from = from.add(batchSize)) {
                BigInteger to = from.add(batchSize);
                if (to.compareTo(last) == 1) {
                    to = last;
                }
                System.out.println("SYNC " + from + " - " + to);
                // erc20TokenWrapper
                // .approvalEventObservable(Web3Util.block(from), Web3Util.block(to))
                // .takeWhile(new Func1<ApprovalEventResponse, Boolean>() {
                // @Override
                // public Boolean call(ApprovalEventResponse approval) {
                // LOGGER.info("takewhile");
                // int compareTo = approval._block.compareTo(syncStatusInfo.getTo());
                // return compareTo < 1;
                // }
                // })
                // .doOnError(exception -> {
                // LOGGER.error("Exception while syncing", exception);
                // })
                // .doOnSubscribe(() -> {
                // LOGGER.info("START");
                // })
                // .doOnCompleted(() -> {
                // System.out.println("DONE");
                // })
                // .toBlocking()
                // .subscribe(approval -> {
                // LOGGER.info(
                // approval._owner.getValue() + "\n" + approval._spender.getValue() + "\n"
                // + approval._value.getValue());
                // });
                erc20TokenWrapper
                        .transferEventObservable(Web3Util.block(from), Web3Util.block(to))
                        .takeWhile(new Func1<TransferEventResponse, Boolean>() {
                            @Override
                            public Boolean call(TransferEventResponse txnForCheck) {
                                if (syncStatusInfo.getTo() == null) {
                                    // null value for to indicates that syncing should be done forever
                                    return true;
                                }
                                int compareTo = txnForCheck._block.compareTo(syncStatusInfo.getTo());
                                return compareTo < 1;
                            }
                        })
                        .doOnError(exception -> {
                            successFlag.set(false);
                            sw.stop();
                            sw.getLastTaskTimeMillis();
                            LOGGER.error("Exception while syncing " + syncStatusInfo, exception);
                            syncStatusInfo.setStatus(SyncStatus.ABORTED);
                            syncStatusInfoRepository.save(syncStatusInfo);
                        })
                        .doOnCompleted(() -> {
                            System.out.println("DONE REAL");
                            syncStatusInfo.setCurrentBlock(syncStatusInfo.getTo());
                            syncStatusInfoRepository.save(syncStatusInfo);
                        })
                        .doOnSubscribe(() -> {
                            System.out.println("START REAL");
                        })
                        .toBlocking()
                        .subscribe(txn -> {
                            syncStatusInfo.setCurrentBlock(txn._block);
                            Address sendFrom = new Address(txn._from.getValue());
                            Address sendTo = new Address(txn._to.getValue());
                            sendFrom.transfer(
                                    sendTo,
                                    syncStatusInfo.getContractAdress(),
                                    txn._value.getValue(),
                                    txn._transactionHash,
                                    txn._block);
                            addressRepository.save(sendFrom);
                            syncStatusInfoRepository.save(syncStatusInfo);
                        });
            }
            // Event event = new Event("Transfer", Collections.emptyList(),
            // Collections.emptyList());
            // EthFilter ethFilter = new EthFilter(
            // block(syncStatusInfo.getFrom()),
            // block(syncStatusInfo.getTo()),
            // syncStatusInfo.getContractAdress());
            // // String encodedEventSignature = EventEncoder.encode(event);
            // ethFilter.addSingleTopic("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef");
            // EthLog logs = web3.ethGetLogs(ethFilter).send();
            // for (LogResult<?> log : logs.getResult()) {
            // Object result = log.get();
            // if (log instanceof Log) {
            // Log l = (Log) result;
            // } else {
            // System.out.println("OTHER CLASS");
            // System.out.println(log.getClass());
            // System.out.println(log);
            // }
            // }
            if (successFlag.get()) {
                sw.stop();
                syncStatusInfo.setDurationInMilliseconds(sw.getLastTaskTimeMillis());
                syncStatusInfo.setStatus(SyncStatus.FINISHED);
            }
        } catch (Exception e) {
            LOGGER.error("Exception while syncing " + syncStatusInfo, e);
            syncStatusInfo.setStatus(SyncStatus.ABORTED);
            throw e;
        } finally {
            syncStatusInfoRepository.save(syncStatusInfo);
        }
        LOGGER.debug("Exiting sync method for " + syncStatusInfo);
    }

}
