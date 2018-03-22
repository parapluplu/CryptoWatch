package de.paraplu.cryptocurrency.domain.neo4j.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;

import de.paraplu.cryptocurrency.domain.neo4j.pojo.Address;

public interface AddressRepository extends Neo4jRepository<Address, String> {

}
