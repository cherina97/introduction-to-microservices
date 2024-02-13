package com.example.resourceprocessor.service;

import com.example.resourceprocessor.model.Song;
import com.example.resourceprocessor.parser.SongParser;
import io.github.resilience4j.decorators.Decorators;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ProcessorServiceImpl implements ProcessorService {

    private final RestClient restClient;
    private final SongParser songParser;

    @Value("${url.call.resources}")
    private String callResource;

    @Value("${url.call.songs}")
    private String callSongs;

    @Value("${url.call.resources.process}")
    private String callResourceForProcess;

    public ProcessorServiceImpl(RestClient restClient, SongParser songParser) {
        this.restClient = restClient;
        this.songParser = songParser;
    }

    public void consume(String resourceId) throws TikaException, IOException, SAXException {
        //call resource service
        byte[] bytes = callResourceService(resourceId);
        Song parsedSong = songParser.parseBytes(bytes, Long.parseLong(resourceId));

        //call song service
        callSongService(parsedSong);

        //trigger resource service with song id to move file to permanent
        callResourceServiceForProcessing(parsedSong.getResourceId());
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

    public byte[] callResourceService(String resourceId) {
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

    public void callResourceServiceForProcessing(Long resourceId) {
        restClient.get()
                .uri(callResourceForProcess + resourceId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(String.class);
    }
}
