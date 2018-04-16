package de.paraplu.cryptocurrency.txnprocessor.triggers;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.paraplu.cryptocurrency.domain.EnrichedTransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger.TriggerEvent;
import de.paraplu.cryptocurrency.util.CryptoConverter.EthConverter;

@Service
public class GasPriceTriggerCheck implements TriggerCheck {

    private BigInteger minValue = BigInteger.valueOf(45000000000l);

    @Override
    public Optional<TriggerEvent> check(EnrichedTransferMessage message) {
        BigInteger gasPrice = message.getTransactionDetails().getGasPrice();
        if (gasPrice.compareTo(minValue) >= 0) {
            final TriggerEvent event = TriggerEvent.txnBasedTriggerEvent(
                    "Gas Price Trigger",
                    "txn used a gas price of " + EthConverter.weiToGwei(gasPrice) + " GWEI",
                    message);
            return Optional.of(event);
        }
        return Optional.empty();
    }

}
