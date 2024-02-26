package com.example.resourceservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MessageListenerService {

    private final ResourceService resourceService;

    public MessageListenerService(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @KafkaListener(topics = "resource-consume-topic", groupId = "processor")
    public void consumeWithRetry(String resourceId) {
        log.info("[Resource Service] Received id from resource processor");
        resourceService.moveResourceToPermanent(Long.valueOf(resourceId));
    }
}
