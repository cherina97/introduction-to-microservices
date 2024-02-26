package com.example.resourceprocessor;

import com.example.resourceprocessor.model.Song;
import com.example.resourceprocessor.service.ProcessorService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureStubRunner(
        ids = "com.example:song-service:+:stubs:8082",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
public class SongServiceContractTest {

    @MockBean
    ProcessorService processorService;

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    public void pingStub() {
        ResponseEntity<Void> response = restTemplate.getForEntity("http://localhost:8082/ping", Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void addSong() {
        Song song = new Song("name", "artist", "album", "length", "year", 1L);

        var response = restTemplate.postForEntity("http://localhost:8082/songs", song, Integer.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo(1);
    }
}
