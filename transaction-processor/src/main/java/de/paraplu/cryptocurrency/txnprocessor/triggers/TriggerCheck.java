package de.paraplu.cryptocurrency.txnprocessor.triggers;

import java.util.Optional;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.EnrichedTransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger.TriggerEvent;

public interface TriggerCheck {
    Optional<TriggerEvent> check(EnrichedTransferMessage message);
}
