package com.example.resourceservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topic}")
    private String topicUpload;

    @Bean
    public NewTopic resourceUpload() {
        return TopicBuilder.name(topicUpload).build();
    }
}
