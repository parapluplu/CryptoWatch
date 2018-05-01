package de.paraplu.cryptocurrency.txnprocessor.triggers;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.EnrichedTransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.Exchange;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger.TriggerEvent;
import de.paraplu.cryptocurrency.domain.mongodb.repository.ExchangeRepository;
import de.paraplu.cryptocurrency.util.CryptoConverter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExchangeTransferTriggerCheck implements TriggerCheck {

    @Autowired
    private ExchangeRepository    exchangeRepository;

    private Map<String, Exchange> exchanges = new HashMap<>();

    @Override
    public Optional<TriggerEvent> check(EnrichedTransferMessage message) {
        boolean triggered = false;
        String description = "";
        Exchange exchange = null;
        if (exchanges.containsKey(message.getTransferMessage().getFrom())) {
            triggered = true;
            description = "<OUT ";
            exchange = exchanges.get(message.getTransferMessage().getFrom());
        } else if (exchanges.containsKey(message.getTransferMessage().getTo())) {
            triggered = true;
            description = ">IN  ";
            exchange = exchanges.get(message.getTransferMessage().getTo());
        }
        if (triggered) {
            description += "[" + exchange.getName() + "]";
            description += NumberFormat.getInstance().format(
                    CryptoConverter
                            .normalize(message.getTransferMessage().getAmount(), message.getTokenInfo().getDecimals()));
            description += " " + message.getTokenInfo().getSymbol();
            TriggerEvent triggerEvent = TriggerEvent
                    .txnBasedTriggerEvent("Exchange transfer trigger", description, message);
            return Optional.of(triggerEvent);
        }
        return Optional.empty();
    }

    @PostConstruct
    public void init() {
        List<Exchange> exchanges = exchangeRepository.findAll();
        if (exchanges.isEmpty()) {
            log.warn("Cannot find any configured exchange.");
        }
        exchanges.forEach(exchange -> {
            exchange.getAddresses().forEach(exchangeAddress -> {
                this.exchanges.put(exchangeAddress, exchange);
            });
        });
    }

}
