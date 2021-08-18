package com.spaceymonk.mentorhub.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;


/**
 * Localization configurer class for this application.
 * Currently, it only sets timezone config.
 *
 * @author spaceymonk
 * @version 1.0, 08/17/21
 */
@Configuration
public class LocaleConfigurer {

    /**
     * Configure application timezone to Europe/Istanbul.
     * Timezone hard-coded, it should be handled properly in future releases.
     */
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Istanbul"));
    }

    /**
     * Configure save timezone to Europe/Istanbul.
     * Timezone hard-coded, it should be handled properly in future releases.
     *
     * @return object mapper with updated time zones
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonObjectMapperCustomization() {
        return jacksonObjectMapperBuilder ->
                jacksonObjectMapperBuilder.timeZone(TimeZone.getTimeZone("Europe/Istanbul"));
    }
}