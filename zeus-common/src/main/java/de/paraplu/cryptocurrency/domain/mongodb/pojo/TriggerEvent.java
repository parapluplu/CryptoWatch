package de.paraplu.cryptocurrency.domain.mongodb.pojo;

import java.time.Instant;

import org.springframework.data.annotation.Id;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class TriggerEvent {
    @Id
    @Setter(AccessLevel.NONE)
    private String  id;
    private String  triggerId;
    private String  description;
    private Instant date;

    public TriggerEvent(String triggerId, String description, Instant date) {
        super();
        this.triggerId = triggerId;
        this.description = description;
        this.date = date;
    }

}
