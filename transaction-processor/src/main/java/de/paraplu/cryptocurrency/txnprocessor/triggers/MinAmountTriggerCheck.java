package de.paraplu.cryptocurrency.txnprocessor.triggers;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.EnrichedTransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger.TriggerEvent;
import de.paraplu.cryptocurrency.util.CryptoConverter;

@Service
public class MinAmountTriggerCheck extends TriggerCheckPerToken {

    private static final String MIN_AMOUNT_CONFIG = "minAmount";

    @Override
    public Optional<TriggerEvent> check(EnrichedTransferMessage message, Map<String, String> config) {
        BigInteger minAmount = new BigInteger(config.get(MIN_AMOUNT_CONFIG));
        BigInteger amount = message.getTransferMessage().getAmount();
        if (amount.compareTo(minAmount) >= 0) {
            String niceAmount = NumberFormat.getInstance().format(
                    CryptoConverter.normalize(amount, message.getTokenInfo().getDecimals()).doubleValue());
            final TriggerEvent event = TriggerEvent.txnBasedTriggerEvent(
                    "Minimum amount trigger",
                    "transfered " + niceAmount + " " + message.getTokenInfo().getSymbol(),
                    message);
            return Optional.of(event);
        }
        return Optional.empty();
    }

}
