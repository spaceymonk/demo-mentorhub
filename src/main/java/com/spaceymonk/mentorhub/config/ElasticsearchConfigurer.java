package com.spaceymonk.mentorhub.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;


/**
 * Configurer class for Elasticsearch integration.
 * Consists of single bean which sets connection address.
 *
 * @author spaceymonk
 * @version 1.0, 08/17/21
 */
@Configuration
public class ElasticsearchConfigurer {

    private final String HOST;
    private final String PORT;

    @Autowired
    public ElasticsearchConfigurer(@Value("${ELASTICSEARCH_HOST:localhost}") String HOST,
                                   @Value("${ELASTICSEARCH_PORT:9200}") String PORT) {
        this.HOST = HOST;
        this.PORT = PORT;
    }

    /**
     * Configures connection host and port for the Elasticsearch instance.
     *
     * @return Configuration details
     */
    @Bean
    RestHighLevelClient elasticsearchClient() {
        System.out.println(HOST+"SEX");
        final ClientConfiguration clientConfiguration =
                ClientConfiguration.builder().connectedTo(HOST + ":" + PORT).build();
        return RestClients.create(clientConfiguration).rest();
    }
}
