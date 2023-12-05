package com.example.songservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ResourceConfig {

    @Value("${resourceCallSong.url}")
    private String addressBaseUrl;

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .baseUrl(addressBaseUrl)
                .build();
    }
}
