package de.paraplu.cryptocurrency.sync.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;
import org.web3j.protocol.Web3j;
import org.web3j.tx.ClientTransactionManager;

import de.paraplu.cryptocurrency.domain.Erc20TokenWrapper;
import de.paraplu.cryptocurrency.domain.TransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.SyncStatusInfo;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.TokenInfo;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.meta.SyncStatus;
import de.paraplu.cryptocurrency.domain.mongodb.repository.SyncStatusInfoRepository;
import de.paraplu.cryptocurrency.domain.mongodb.repository.TokenInfoRepository;
import de.paraplu.cryptocurrency.domain.neo4j.pojo.Address;
import de.paraplu.cryptocurrency.domain.neo4j.pojo.Transfer;
import de.paraplu.cryptocurrency.sync.util.Web3Util;

@Component
public class SyncService {

    private static final Logger      LOGGER = LoggerFactory.getLogger(SyncService.class);

    @Autowired
    private SyncStatusInfoRepository syncStatusInfoRepository;
    @Autowired
    private TokenInfoRepository      tokenInfoRepository;

    @Autowired
    private Source                   source;

    @Autowired
    private Web3j                    web3;

    @Value(value = "${sync.batchSize}")
    private BigInteger               batchSize;

    private void saveTokenInfo(Erc20TokenWrapper erc20TokenWrapper)
            throws InterruptedException, ExecutionException, IOException {
        String symbol = erc20TokenWrapper.symbol().getValue();
        BigInteger decimals = erc20TokenWrapper.decimals().getValue();
        String address = erc20TokenWrapper.getContractAddress();
        TokenInfo tokenInfo = new TokenInfo(address, symbol, decimals);
        tokenInfoRepository.save(tokenInfo);
    }

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
            StopWatch sw = new StopWatch("sync");
            sw.start();
            syncStatusInfo.setStatus(SyncStatus.SYNCING);
            syncStatusInfoRepository.save(syncStatusInfo);
            BigInteger first = syncStatusInfo.getFrom();
            BigInteger last = syncStatusInfo.getTo();
            final AtomicBoolean successFlag = new AtomicBoolean(true);

            try {
                saveTokenInfo(erc20TokenWrapper);
            } catch (ExecutionException | IOException e) {
                syncStatusInfo.setStatus(SyncStatus.ABORTED);
                String msg = "Unable to retrieve token details. Won't start syncing";
                LOGGER.error(msg, e);
                throw new SyncServiceException(msg, e);
            }
            //
            // for (BigInteger from = first; from.compareTo(last) == -1; from =
            // from.add(batchSize)) {
            BigInteger to = first.add(batchSize);
            System.out.println("SYNC " + first + " - " + to);
            erc20TokenWrapper
                    .transferEventObservable(Web3Util.block(first), Web3Util.block(to))
                    // .takeWhile(new Func1<TransferEventResponse, Boolean>() {
                    // @Override
                    // public Boolean call(TransferEventResponse txnForCheck) {
                    // if (syncStatusInfo.getTo() == null) {
                    // // null value for to indicates that syncing should be done forever
                    // return true;
                    // }
                    // int compareTo = txnForCheck._block.compareTo(syncStatusInfo.getTo());
                    // return compareTo < 1;
                    // }
                    // })
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
                        Transfer transfer = sendFrom.transfer(
                                sendTo,
                                syncStatusInfo.getContractAdress(),
                                txn._value.getValue(),
                                txn._transactionHash,
                                txn._block);
                        TransferMessage transferMessage = new TransferMessage(transfer);
                        source.output().send(new GenericMessage<>(transferMessage));
                        syncStatusInfo.setCurrentBlock(txn._block);
                        syncStatusInfoRepository.save(syncStatusInfo);
                    });
            // }
            // if (successFlag.get()) {
            // sw.stop();
            // syncStatusInfo.setDurationInMilliseconds(sw.getLastTaskTimeMillis());
            // syncStatusInfo.setStatus(SyncStatus.FINISHED);
            // }
        } catch (InterruptedException e) {
            LOGGER.warn("Syncing process got interrupted for " + syncStatusInfo);
            syncStatusInfo.setStatus(SyncStatus.ABORTED);
            // no need to rethrow, since we are in an async method
        } catch (Exception e) {
            syncStatusInfo.setStatus(SyncStatus.ABORTED);
            LOGGER.error("Error while syncing", e);
            throw e;
        } finally {
            syncStatusInfoRepository.save(syncStatusInfo);
        }
        LOGGER.debug("Exiting sync method for " + syncStatusInfo);
    }

}
