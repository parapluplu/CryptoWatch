package de.paraplu.cryptocurrency.domain.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.trigger.TriggerEvent;

@Repository
public interface TriggerEventRepository extends MongoRepository<TriggerEvent, String> {

}
