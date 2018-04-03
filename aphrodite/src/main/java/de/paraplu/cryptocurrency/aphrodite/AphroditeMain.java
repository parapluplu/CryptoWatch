package de.paraplu.cryptocurrency.aphrodite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import de.paraplu.cryptocurrency.domain.mongodb.repository.SyncStatusInfoRepository;
import de.paraplu.cryptocurrency.domain.neo4j.repository.AddressRepository;

@SpringBootApplication
@EnableMongoRepositories(basePackageClasses = SyncStatusInfoRepository.class)
@EnableNeo4jRepositories(basePackageClasses = AddressRepository.class)
public class AphroditeMain {
    public static void main(String[] args) {
        SpringApplication.run(AphroditeMain.class, args);
    }
}
