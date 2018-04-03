package de.paraplu.cryptocurrency.txnprocessor;

import java.math.BigInteger;
import java.time.Instant;
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

import de.paraplu.cryptocurrency.domain.EnrichedTransferMessage;
import de.paraplu.cryptocurrency.domain.TransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.TokenInfo;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.TriggerEvent;
import de.paraplu.cryptocurrency.domain.mongodb.repository.SyncStatusInfoRepository;
import de.paraplu.cryptocurrency.domain.mongodb.repository.TokenInfoRepository;
import de.paraplu.cryptocurrency.domain.mongodb.repository.TriggerEventRepository;
import de.paraplu.cryptocurrency.domain.neo4j.pojo.Address;
import de.paraplu.cryptocurrency.domain.neo4j.repository.AddressRepository;

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
    private AddressRepository      addressRepository;

    @Autowired
    private TriggerEventRepository triggerEventRepository;

    @Autowired
    private TokenInfoRepository    tokenInfoRepository;

    private void action(TriggerEvent triggerEvent) {
        triggerEventRepository.save(triggerEvent);
        // TODO
    }

    private EnrichedTransferMessage enrich(TransferMessage transferMessage) {
        Optional<TokenInfo> tokenInfoOptional = tokenInfoRepository.findById(transferMessage.getTokenAddress());
        if (tokenInfoOptional.isPresent()) {
            EnrichedTransferMessage enrichedTransferMessage = new EnrichedTransferMessage(
                    transferMessage,
                    tokenInfoOptional.get());
            return enrichedTransferMessage;
        } else {
            LOGGER.error("Cannot process. No token info present for " + transferMessage);
            return null;
        }
    }

    private TriggerEvent infere(EnrichedTransferMessage enriched) {
        LOGGER.info("TXN amount is " + enriched.getTransferMessage().getAmount());
        BigInteger decimals = enriched.getTokenInfo().getDecimals();
        if (enriched.getTransferMessage().getAmount().compareTo(
                BigInteger.TEN.pow(decimals.intValue()).multiply(BigInteger.valueOf(1000000))) >= 0) {
            TriggerEvent event = new TriggerEvent(
                    "My custom million trigger",
                    enriched.getTransferMessage().getTransaction(),
                    Instant.now());
            return event;
        }
        return null;
    }

    @StreamListener(Sink.INPUT)
    public void receive(TransferMessage transferMessage) {
        // all in one service
        store(transferMessage);
        EnrichedTransferMessage enrichedTransferMessage = enrich(transferMessage);
        TriggerEvent triggerEvent = infere(enrichedTransferMessage);
        if (triggerEvent != null) {
            action(triggerEvent);
        }

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
