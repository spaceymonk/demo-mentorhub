package com.spaceymonk.mentorhub.config;

import org.elasticsearch.client.RestHighLevelClient;
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

    /**
     * Configures connection host and port for the Elasticsearch instance.
     *
     * @return Configuration details
     */
    @Bean
    RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration =
                ClientConfiguration.builder().connectedTo("elasticsearch:9200").build();
        return RestClients.create(clientConfiguration).rest();
    }
}
