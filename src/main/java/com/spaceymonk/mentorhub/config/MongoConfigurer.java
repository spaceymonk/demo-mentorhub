package com.spaceymonk.mentorhub.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfigurer {


    private final String HOST;
    private final String PORT;

    public MongoConfigurer(@Value("${MONGO_HOST:localhost}") String HOST,
                           @Value("${MONGO_PORT:27017}") String PORT) {
        this.HOST = HOST;
        this.PORT = PORT;
    }

    @Bean
    public MongoClient mongo() {
        ConnectionString connectionString = new ConnectionString("mongodb://" + HOST + ":" + PORT + "/mentorhub");
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .build();

        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongo(), "mentorhub");
    }
}
