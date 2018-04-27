package de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.annotation.Id;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.EnrichedTransferMessage;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class TriggerEvent {

    private static final String TXN_HASH = "txnHash";
    private static final String SYMBOL   = "symbol";

    public static TriggerEvent txnBasedTriggerEvent(String triggerId, String description,
            EnrichedTransferMessage enrichedTransferMessage) {
        Map<String, Object> data = new HashMap<>();
        data.put(TXN_HASH, enrichedTransferMessage.getTxnHash());
        data.put(SYMBOL, enrichedTransferMessage.getTokenInfo().getSymbol());
        return new TriggerEvent(triggerId, description, Instant.now(), "txnBasedTriggerEvent", data);
    }

    @Id
    @Setter(AccessLevel.NONE)
    private String                id;
    private String                triggerId;
    private String                eventType;
    protected Map<String, Object> data;
    private String                description;

    private Instant               date;

    public TriggerEvent() {
        // empty constructor
    }

    private TriggerEvent(
            String triggerId,
            String description,
            Instant date,
            String eventType,
            Map<String, Object> data) {
        super();
        this.triggerId = triggerId;
        this.description = description;
        this.date = date;
        this.data = data;
        this.eventType = eventType;
    }

}
