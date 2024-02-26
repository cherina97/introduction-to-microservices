package com.example.resourceprocessor.service;

import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Component
public class MessageListenerService {

    private final ProcessorService processorService;

    public MessageListenerService(ProcessorService processorService) {
        this.processorService = processorService;
    }

    @KafkaListener(topics = "resource-topic", groupId = "processor")
    public void consumeWithRetry(String resourceId) {
        Decorators.ofRunnable(() -> {
                    try {
                        processorService.consume(resourceId);
                    } catch (TikaException | IOException | SAXException e) {
                        throw new RuntimeException(e);
                    }
                })
                .withRetry(retry())
                .run();
    }

    private Retry retry() {
        Retry retry = Retry.of("ProcessorServiceImpl", RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofSeconds(3))
                .build());

        retry.getEventPublisher().onEvent(event -> log.info("Retry event: " + event));
        return retry;
    }
}
