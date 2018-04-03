package de.paraplu.cryptocurrency.domain.mongodb.pojo;

import java.math.BigInteger;
import java.time.Instant;

import org.springframework.data.annotation.Id;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.meta.SyncStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SyncStatusInfo {

    @Id
    @Setter(AccessLevel.NONE)
    private String     id;
    private String     contractAdress;
    private BigInteger currentBlock;
    private BigInteger from;
    private BigInteger to;
    private SyncStatus status;
    private Instant    started;
    private Instant    updated;
    private long       durationInMilliseconds;

}
