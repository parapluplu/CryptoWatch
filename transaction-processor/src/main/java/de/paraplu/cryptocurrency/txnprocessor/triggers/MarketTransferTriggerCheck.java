package de.paraplu.cryptocurrency.txnprocessor.triggers;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.paraplu.cryptocurrency.domain.TransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.EnrichedTransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger.TriggerEvent;

@Service
public class MarketTransferTriggerCheck implements TriggerCheck {

    private static final BigInteger DECIMAL_NORMALIZER = BigInteger.TEN.pow(18);
    private String                  token              = "0x86fa049857e0209aa7d9e616f7eb3b3b78ecfdb0";
    private String                  marketAddress      = "0x876EabF441B2EE5B5b0554Fd502a8E0600950cFa";         // EOS
    private BigInteger              minValue           = BigInteger.valueOf(1000).multiply(DECIMAL_NORMALIZER);

    @Override
    public Optional<TriggerEvent> check(EnrichedTransferMessage message) {
        TransferMessage transfer = message.getTransferMessage();
        boolean out = transfer.getFrom().equals(marketAddress);
        boolean in = transfer.getTo().equals(marketAddress);
        if (message.getTokenInfo().getAddress().equals(token) && (out ^ in)
                && transfer.getAmount().compareTo(minValue) >= 0) {
            String direction = out ? "from" : "to";
            final TriggerEvent event = TriggerEvent.txnBasedTriggerEvent(
                    "Marketplace transaction trigger",
                    "txn " + direction + " exchange wallet with amount >= " + minValue.divide(DECIMAL_NORMALIZER),
                    message);
            return Optional.of(event);
        }
        return Optional.empty();
    }

}
