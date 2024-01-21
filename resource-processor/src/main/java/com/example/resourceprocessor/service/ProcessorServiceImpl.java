package com.example.resourceprocessor.service;

import com.example.resourceprocessor.model.Song;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.time.Duration;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Service
public class ProcessorServiceImpl extends AbstractSongParser {

    private final RestClient restClient;

    @Value("${url.call.resources}")
    private String callResource;

    @Value("${url.call.songs}")
    private String callSongs;

    public ProcessorServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    public void consume(String resourceId) throws TikaException, IOException, SAXException {
        byte[] bytes = callResourceService(resourceId);
        Song parsedSong = parseBytes(bytes, Long.parseLong(resourceId));
        callSongService(parsedSong);
    }

    @KafkaListener(topics = "resource-topic", groupId = "processor")
    public void consumeWithRetry(String resourceId) {
        Decorators.ofRunnable(() -> {
                    try {
                        consume(resourceId);
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

    private byte[] callResourceService(String resourceId) {
        return restClient
                .get()
                .uri(callResource + "/" + resourceId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(byte[].class);
    }

    private void callSongService(Song parsedSong) {
        restClient.post()
                .uri(callSongs)
                .contentType(APPLICATION_JSON)
                .body(parsedSong)
                .retrieve()
                .toBodilessEntity();
    }
}
