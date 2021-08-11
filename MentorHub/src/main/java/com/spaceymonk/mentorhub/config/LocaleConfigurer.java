package com.spaceymonk.mentorhub.config;


import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@Configuration
public class LocaleConfigurer {

    @PostConstruct
    public void init() {

        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Istanbul"));
    }

}