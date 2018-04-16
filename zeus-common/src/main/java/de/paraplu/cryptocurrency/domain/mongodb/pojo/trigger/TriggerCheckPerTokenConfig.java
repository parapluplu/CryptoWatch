package de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger;

import java.util.Map;

import org.springframework.data.annotation.Id;

import lombok.Data;

@Data
public class TriggerCheckPerTokenConfig {
    @Id
    private String                           trigger;
    private Map<String, Map<String, String>> tokenConfigs;
}
