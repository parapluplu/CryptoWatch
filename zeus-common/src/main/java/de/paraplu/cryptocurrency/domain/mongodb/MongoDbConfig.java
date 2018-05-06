package de.paraplu.cryptocurrency.domain.mongodb;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.convert.Jsr310Converters.LocalDateTimeToDateConverter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

@Configuration
public class MongoDbConfig {

    @Autowired
    private MongoDbFactory mongoDbFactory;

    @Bean
    public MongoTemplate mongoTemplate() {
        MappingMongoConverter converter = new MappingMongoConverter(
                new DefaultDbRefResolver(mongoDbFactory),
                new MongoMappingContext());
        converter
                .setCustomConversions(new MongoCustomConversions(Arrays.asList(LocalDateTimeToDateConverter.INSTANCE)));

        return new MongoTemplate(mongoDbFactory, converter);
    }
}