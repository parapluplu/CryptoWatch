package de.paraplu.cryptocurrency.sync.service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
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
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.Contract;

import de.paraplu.cryptocurrency.domain.Erc20TokenWrapper;
import de.paraplu.cryptocurrency.domain.Erc20TokenWrapper.TransferEventResponse;
import de.paraplu.cryptocurrency.domain.TransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.SyncStatusInfo;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.TokenInfo;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.meta.SyncStatus;
import de.paraplu.cryptocurrency.domain.mongodb.repository.SyncStatusInfoRepository;
import de.paraplu.cryptocurrency.domain.mongodb.repository.TokenInfoRepository;
import de.paraplu.cryptocurrency.domain.neo4j.pojo.Address;
import de.paraplu.cryptocurrency.domain.neo4j.pojo.Transfer;
import lombok.SneakyThrows;

@Component
public class SyncService {

    static {
        Event e = new Event(
                "Transfer",
                Arrays.<TypeReference<?>>asList(new TypeReference<org.web3j.abi.datatypes.Address>() {
                }, new TypeReference<org.web3j.abi.datatypes.Address>() {
                }),
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {
                }));
        TRANSFER_EVENT = e;
        TRANSFER_EVENT_TOPIC = EventEncoder.encode(e);
    }

    private static final Event       TRANSFER_EVENT;
    private static final String      TRANSFER_EVENT_TOPIC;
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

    private TransferEventResponse extractTransfer(Log log) {
        EventValues eventValues = Contract.staticExtractEventParameters(TRANSFER_EVENT, log);
        TransferEventResponse txn = new TransferEventResponse();
        txn._from = (org.web3j.abi.datatypes.Address) eventValues.getIndexedValues().get(0);
        txn._to = (org.web3j.abi.datatypes.Address) eventValues.getIndexedValues().get(1);
        txn._value = (Uint256) eventValues.getNonIndexedValues().get(0);
        txn._block = log.getBlockNumber();
        txn._transactionHash = log.getTransactionHash();
        return txn;
    }

    private void saveTokenInfo(Erc20TokenWrapper erc20TokenWrapper)
            throws InterruptedException, ExecutionException, IOException {
        Optional<TokenInfo> optionalTokenInfo = tokenInfoRepository.findById(erc20TokenWrapper.getContractAddress());
        TokenInfo tokenInfo = optionalTokenInfo.orElse(new TokenInfo(erc20TokenWrapper.getContractAddress()));
        Utf8String symbolRaw = erc20TokenWrapper.symbol();
        if (symbolRaw != null) {
            String symbol = symbolRaw.getValue();
            if (!symbol.trim().isEmpty()) {
                tokenInfo.setSymbol(symbol);
            }
        } else if (tokenInfo.getSymbol() == null) {
            LOGGER.warn("No symbol retrievable for token " + tokenInfo.getAddress());
        }
        Uint8 decimalRaw = erc20TokenWrapper.decimals();
        if (decimalRaw != null) {
            BigInteger decimals = decimalRaw.getValue();
            if (decimals != null) {
                tokenInfo.setDecimals(decimals);
            }
        } else if (tokenInfo.getDecimals() == null) {
            LOGGER.warn(
                    "No decimals retrievable for token " + tokenInfo.getAddress() + "(" + tokenInfo.getSymbol() + ")");
        }
        tokenInfoRepository.save(tokenInfo);
    }

    @SneakyThrows
    @Async
    public CompletableFuture<SyncStatusInfo> sync(TokenInfo token) {
        SyncStatusInfo syncStatusInfoInit = new SyncStatusInfo();
        syncStatusInfoInit.setContractAdress(token.getAddress());
        syncStatusInfoInit.setStatus(SyncStatus.SYNCING);
        final SyncStatusInfo syncStatusInfo = syncStatusInfoRepository.save(syncStatusInfoInit);
        try {
            ClientTransactionManager transactionManager = new ClientTransactionManager(web3, token.getAddress());
            Erc20TokenWrapper erc20TokenWrapper = Erc20TokenWrapper
                    .load(token.getAddress(), web3, transactionManager, BigInteger.ZERO, BigInteger.ZERO);
            StopWatch sw = new StopWatch("sync");
            sw.start();
            final AtomicBoolean successFlag = new AtomicBoolean(true);

            try {
                saveTokenInfo(erc20TokenWrapper);
            } catch (ExecutionException | IOException e) {
                syncStatusInfo.setStatus(SyncStatus.ABORTED);
                String msg = "Unable to retrieve token details. Won't start syncing. Token info " + token;
                LOGGER.error(msg, e);
                throw new SyncServiceException(msg, e);
            }
            EthFilter filter = new EthFilter(
                    DefaultBlockParameterName.LATEST,
                    DefaultBlockParameterName.LATEST,
                    token.getAddress());
            filter.addSingleTopic(TRANSFER_EVENT_TOPIC);
            web3.ethLogObservable(filter).doOnError(exception -> {
                successFlag.set(false);
                sw.stop();
                sw.getLastTaskTimeMillis();
                LOGGER.error("Exception while syncing " + syncStatusInfo, exception);
                syncStatusInfo.setStatus(SyncStatus.ABORTED);
                syncStatusInfoRepository.save(syncStatusInfo);
            }).doOnCompleted(() -> {
                syncStatusInfo.setCurrentBlock(syncStatusInfo.getTo());
                syncStatusInfoRepository.save(syncStatusInfo);
            }).doOnSubscribe(() -> {
                LOGGER.info("Start syncing " + token.getSymbol());
            }).toBlocking().subscribe(log -> {
                syncStatusInfo.setCurrentBlock(log.getBlockNumber());
                TransferEventResponse txn = extractTransfer(log);
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
            }, error -> {
                successFlag.set(false);
                sw.stop();
                sw.getLastTaskTimeMillis();
                LOGGER.error("Exception while syncing " + token.getSymbol(), error);
                syncStatusInfo.setStatus(SyncStatus.ABORTED);
                syncStatusInfoRepository.save(syncStatusInfo);
            }, () -> {
                LOGGER.info("Completed syncing " + token.getSymbol());
            });
        } catch (InterruptedException e) {
            LOGGER.warn("Syncing process got interrupted for " + syncStatusInfo);
            syncStatusInfo.setStatus(SyncStatus.ABORTED);
            Thread.currentThread().interrupt();
            // no need to rethrow, since we are in an async method
        } catch (Exception e) {
            syncStatusInfo.setStatus(SyncStatus.ABORTED);
            LOGGER.error("Error while syncing", e);
            throw e;
        } finally {
            syncStatusInfoRepository.save(syncStatusInfo);
        }
        LOGGER.debug("Exiting sync method for " + syncStatusInfo);
        return CompletableFuture.completedFuture(syncStatusInfo);
    }

}
