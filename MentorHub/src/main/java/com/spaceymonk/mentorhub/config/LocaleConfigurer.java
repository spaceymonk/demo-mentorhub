package com.spaceymonk.mentorhub.config;

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
}