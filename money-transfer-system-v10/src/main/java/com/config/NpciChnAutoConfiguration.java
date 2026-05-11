package com.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "npci.location", havingValue = "chennai")
public class NpciChnAutoConfiguration {

    @Bean
    public String bean1() {
        return "Bean 1";
    }

}
