package de.paraplu.cryptocurrency.domain.mongodb.repository;

import java.util.Collection;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger.TriggerEvent;

@Repository
public interface TriggerEventRepository extends MongoRepository<TriggerEvent, String> {

    @Query(value = "{'data.symbol': { '$in': ?0 } }")
    public Page<TriggerEvent> findByTokens(@Param("tokenSymbols") Collection<String> tokenSymbol, Pageable pageable);
}
