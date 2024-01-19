package com.example.resourceprocessor.service;

import com.example.resourceprocessor.model.Song;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.exception.TikaException;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.xml.sax.SAXException;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Service
public class ProcessorServiceImpl extends AbstractSongParser {

    private final RestClient restClientCallResources = RestClient.create("http://gateway-service:8080/resources/s3");
    private final RestClient restClientCallSongs = RestClient.builder().baseUrl("http://gateway-service:8080/songs")
            .messageConverters(converters -> converters.add(new MappingJackson2HttpMessageConverter()))
            .build();

    @KafkaListener(topics = "resource-topic", groupId = "processor")
    public void consume(String resourceId) throws TikaException, IOException, SAXException {

        byte[] bytes = callResourceService(resourceId);
        Song parsedSong = parseBytes(bytes, Long.parseLong(resourceId));

        callSongService(parsedSong);
    }

    private byte[] callResourceService(String resourceId) {
        return restClientCallResources.get()
                .uri("/" + resourceId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(byte[].class);
    }

    private void callSongService(Song parsedSong) {
        restClientCallSongs.post()
                .contentType(APPLICATION_JSON)
                .body(parsedSong)
                .retrieve()
                .toBodilessEntity();
    }
}
