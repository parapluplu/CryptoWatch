package de.paraplu.cryptocurrency.txnprocessor.triggers;

import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.paraplu.cryptocurrency.domain.EnrichedTransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger.TriggerEvent;
import de.paraplu.cryptocurrency.util.CryptoConverter;

@Service
public class MinAmountTriggerCheck extends TriggerCheckPerToken {

    private static final String MIN_AMOUNT_CONFIG = "minAmount";

    @Override
    public Optional<TriggerEvent> check(EnrichedTransferMessage message, Map<String, String> config) {
        BigInteger minAmount = new BigInteger(config.get(MIN_AMOUNT_CONFIG));
        if (message.getTransferMessage().getAmount().compareTo(minAmount) >= 0) {
            final TriggerEvent event = TriggerEvent.txnBasedTriggerEvent(
                    "Minimum amount trigger",
                    "txn amount >= " + CryptoConverter.normalize(minAmount, message.getTokenInfo().getDecimals()) + " "
                            + message.getTokenInfo().getSymbol(),
                    message);
            return Optional.of(event);
        }
        return Optional.empty();
    }

}
