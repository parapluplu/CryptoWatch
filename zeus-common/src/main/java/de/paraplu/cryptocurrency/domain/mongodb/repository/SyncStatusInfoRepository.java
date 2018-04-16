package de.paraplu.cryptocurrency.domain.mongodb.repository;

import java.math.BigInteger;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.SyncStatusInfo;
import de.paraplu.cryptocurrency.domain.mongodb.pojo.meta.SyncStatus;

@Repository
public interface SyncStatusInfoRepository extends MongoRepository<SyncStatusInfo, String> {

    Optional<SyncStatusInfo> fromGreaterThanEqualAndStatusNotIn(BigInteger fromBlock, SyncStatus[] notSyncing);

}
