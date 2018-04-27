package de.paraplu.cryptocurrency.txnprocessor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.core.methods.response.Transaction;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.paraplu.cryptocurrency.domain.TransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.EnrichedTransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.TokenInfo;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger.TriggerEvent;
import de.paraplu.cryptocurrency.domain.mongodb.repository.EnrichedTransferMessageRepository;
import de.paraplu.cryptocurrency.domain.mongodb.repository.SyncStatusInfoRepository;
import de.paraplu.cryptocurrency.domain.mongodb.repository.TokenInfoRepository;
import de.paraplu.cryptocurrency.domain.mongodb.repository.TriggerEventRepository;
import de.paraplu.cryptocurrency.domain.neo4j.pojo.Address;
import de.paraplu.cryptocurrency.domain.neo4j.repository.AddressRepository;
import de.paraplu.cryptocurrency.txnprocessor.triggers.TriggerCheck;

@SpringBootApplication
@EnableNeo4jRepositories(basePackageClasses = AddressRepository.class)
@EnableMongoRepositories(basePackageClasses = SyncStatusInfoRepository.class)
@EnableTransactionManagement
@EnableBinding(Sink.class)
public class TransactionProcessorMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionProcessorMain.class);

    public static void main(String[] args) {
        SpringApplication.run(TransactionProcessorMain.class, args);
    }

    @Autowired
    private AddressRepository                 addressRepository;

    @Autowired
    private EnrichedTransferMessageRepository enrichedTransferMessageRepository;

    @Autowired
    private TriggerEventRepository            triggerEventRepository;

    @Autowired
    private TokenInfoRepository               tokenInfoRepository;

    @Autowired
    private List<TriggerCheck>                triggerCheckers;

    @Autowired
    private Web3j                             web3;

    private void action(List<TriggerEvent> triggerEvents, EnrichedTransferMessage message) {
        triggerEventRepository.saveAll(triggerEvents);
        enrichedTransferMessageRepository.save(message);
    }

    private EnrichedTransferMessage enrich(TransferMessage transferMessage) throws IOException {
        Optional<TokenInfo> tokenInfoOptional = tokenInfoRepository.findById(transferMessage.getTokenAddress());
        if (tokenInfoOptional.isPresent()) {
            try {
                Transaction result = web3.ethGetTransactionByHash(transferMessage.getTransaction()).send().getResult();
                Block block = web3
                        .ethGetBlockByNumber(DefaultBlockParameter.valueOf(transferMessage.getBlock()), false)
                        .send()
                        .getBlock();
                EnrichedTransferMessage enrichedTransferMessage = new EnrichedTransferMessage(
                        result.getHash(),
                        transferMessage,
                        tokenInfoOptional.get(),
                        result,
                        block);
                return enrichedTransferMessage;
            } catch (IOException e) {
                LOGGER.error("Error while enriching txn " + transferMessage.getTransaction(), e);
                throw e;
            }
        } else {
            LOGGER.error("Cannot process. No token info present for " + transferMessage);
            return null;
        }
    }

    private List<TriggerEvent> infere(EnrichedTransferMessage enriched) throws JsonProcessingException {
        List<TriggerEvent> triggerEvents = new ArrayList<>();
        for (TriggerCheck checker : triggerCheckers) {
            Optional<TriggerEvent> event = checker.check(enriched);
            if (event.isPresent()) {
                triggerEvents.add(event.get());
            }
        }
        return triggerEvents;
    }

    @StreamListener(Sink.INPUT)
    public void receive(TransferMessage transferMessage) throws IOException {
        // all in one service
        LOGGER.info("Processing message " + transferMessage.getTransaction());
        store(transferMessage);
        EnrichedTransferMessage enrichedTransferMessage = enrich(transferMessage);
        List<TriggerEvent> triggerEvents = infere(enrichedTransferMessage);
        action(triggerEvents, enrichedTransferMessage);
        LOGGER.info("[DONE] " + transferMessage.getTransaction());

    }

    private void store(TransferMessage transferMessage) {
        Address from = new Address(transferMessage.getFrom());
        Address to = new Address(transferMessage.getTo());
        from.transfer(
                to,
                transferMessage.getTokenAddress(),
                transferMessage.getAmount(),
                transferMessage.getTransaction(),
                transferMessage.getBlock());
        // addressRepository.save(from);
    }
}
