package com.example.resourceprocessor.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class AppConfig {

    @Bean
    RestClient getRestClient() {
        return RestClient.create();
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        return new DefaultErrorHandler(
                (consumerRecord, exception) -> log.error("Service unavailable"),
                new FixedBackOff(10000, 2));
    }
}
