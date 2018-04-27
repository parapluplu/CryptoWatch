package de.paraplu.cryptocurrency.domain.mongodb.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import de.paraplu.cryptocurrency.domain.mongodb.pojo.EnrichedTransferMessage;

@Repository
public interface EnrichedTransferMessageRepository extends MongoRepository<EnrichedTransferMessage, String> {

}
