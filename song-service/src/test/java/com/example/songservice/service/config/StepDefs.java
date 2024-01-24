package com.example.songservice.service.config;

import com.example.songservice.model.Song;
import com.example.songservice.service.SongService;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.Assert;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
public class StepDefs {

    @LocalServerPort
    private int port;

    @MockBean
    SongService songService;

    private final RestClient restClient = RestClient.create();

    private ResponseEntity<Void> response;

    @When("the client calls song service")
    public void theClientCallsSongService() {

        Song song = new Song(1L, "Song1", "artist", null, null, null, 1L);

        response = restClient.post()
                .uri("http://localhost:" + port + "/songs")
                .contentType(APPLICATION_JSON)
                .body(song)
                .retrieve()
                .toBodilessEntity();

        Assert.assertNotNull(response);
    }

    @Then("the client receives status code of {int}")
    public void theClientReceivesStatusCodeOf(int statusCode) {
        Assert.assertEquals(statusCode, response.getStatusCode().value());
    }

    @And("the client receives {int} of created song")
    public void theClientReceivesIdOfCreatedSong(int id) {
        int expectedId = 1;
        Assert.assertEquals(expectedId, id);
    }
}
