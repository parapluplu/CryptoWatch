package de.paraplu.cryptocurrency.txnprocessor.triggers;

import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.EnrichedTransferMessage;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger.TriggerCheckPerTokenConfig;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger.TriggerEvent;
import de.paraplu.cryptocurrency.domain.mongodb.repository.TriggerCheckPerTokenConfigRepository;

public abstract class TriggerCheckPerToken implements TriggerCheck {

    private Map<String, Map<String, String>>     configPerToken;

    @Autowired
    private TriggerCheckPerTokenConfigRepository triggerCheckPerTokenConfigRepository;

    @Override
    public Optional<TriggerEvent> check(EnrichedTransferMessage message) {
        Map<String, String> tokenConfig = configPerToken.get(message.getTokenInfo().getAddress());
        if (tokenConfig != null) {
            return check(message, tokenConfig);
        }
        return Optional.empty();
    }

    protected abstract Optional<TriggerEvent> check(EnrichedTransferMessage message, Map<String, String> tokenConfig);

    @PostConstruct
    public void init() {
        // get token configuration
        Optional<TriggerCheckPerTokenConfig> config = triggerCheckPerTokenConfigRepository
                .findById(getClass().getName());
        if (config.isPresent()) {
            configPerToken = config.get().getTokenConfigs();
        } else {
            throw new IllegalStateException("No triggerCheckPerTokenConfig found for triggerCheck " + getClass());
        }
    }

}
