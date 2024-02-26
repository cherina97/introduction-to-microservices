package com.example.resourceprocessor.service;

import com.example.resourceprocessor.model.Song;
import com.example.resourceprocessor.parser.SongParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Service
public class ProcessorService {

    private final RestClient restClient;
    private final SongParser songParser;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${url.call.resources}")
    private String callResource;

    @Value("${url.call.songs}")
    private String callSongs;

    @Value("${url.call.resources.process}")
    private String callResourceForProcess;

    @Value("${kafka.topic}")
    private String topic;

    public ProcessorService(RestClient restClient, SongParser songParser, KafkaTemplate<String, String> kafkaTemplate) {
        this.restClient = restClient;
        this.songParser = songParser;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void consume(String resourceId) throws TikaException, IOException, SAXException {
        //call resource service
        byte[] bytes = callResourceService(resourceId);
        Song parsedSong = songParser.parseBytes(bytes, Long.parseLong(resourceId));

        //call song service
        callSongService(parsedSong);

        //pass message with processed id to the resource service
        kafkaTemplate.send(topic, String.valueOf(parsedSong.getResourceId()));
        log.info("[Resource Processor] Pissing message to the topic " + topic);
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
