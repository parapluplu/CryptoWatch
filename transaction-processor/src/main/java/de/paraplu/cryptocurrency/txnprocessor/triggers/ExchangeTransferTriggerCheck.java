package de.paraplu.cryptocurrency.txnprocessor.triggers;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.paraplu.cryptocurrency.domain.ExchangeInformation;
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
    @Autowired
    private MinAmountTriggerCheck minAmountTriggerCheck;

    private Map<String, Exchange> exchanges = new HashMap<>();

    @Override
    public Optional<TriggerEvent> check(EnrichedTransferMessage message) {
        Exchange exchangeFrom = exchanges.get(message.getTransferMessage().getFrom());
        Exchange exchangeTo = exchanges.get(message.getTransferMessage().getTo());
        if (exchangeFrom != null ^ exchangeTo != null) {
            Optional<TriggerEvent> minAmountCheck = minAmountTriggerCheck.check(message);
            if (minAmountCheck.isPresent()) {
                // only return exchange result, if minimum amount is used
                String description = "Transfered "
                        + NumberFormat.getInstance().format(
                                CryptoConverter.normalize(
                                        message.getTransferMessage().getAmount(),
                                        message.getTokenInfo().getDecimals()))
                        + " " + message.getTokenInfo().getSymbol();
                ExchangeInformation exchangeInformation;
                if (exchangeFrom != null) {
                    description += " from " + exchangeFrom.getName();
                    exchangeInformation = new ExchangeInformation(
                            false,
                            true,
                            message.getTransferMessage().getFrom(),
                            exchangeFrom.getName());
                } else {
                    description += " to " + exchangeTo.getName();
                    exchangeInformation = new ExchangeInformation(
                            true,
                            false,
                            message.getTransferMessage().getTo(),
                            exchangeTo.getName());
                }
                message.setExchange(exchangeInformation);
                TriggerEvent triggerEvent = TriggerEvent
                        .txnBasedTriggerEvent("Exchange transfer trigger", description, message);
                return Optional.of(triggerEvent);
            }
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
                this.exchanges.put(exchangeAddress.toLowerCase(), exchange);
            });
        });
        log.info("EXCHANGES: " + this.exchanges);
    }

    public void setExchanges(Map<String, Exchange> exchanges) {
        this.exchanges = exchanges;
    }

}
