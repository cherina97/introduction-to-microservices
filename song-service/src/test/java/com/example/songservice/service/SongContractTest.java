package com.example.songservice.service;

import com.example.songservice.controller.SongController;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class SongContractTest {

    @Autowired
    private SongController songController;

    @MockBean
    private SongService songService;

    @BeforeEach
    public void setup() {
        StandaloneMockMvcBuilder mockMvcBuilder = MockMvcBuilders.standaloneSetup(songController);
        RestAssuredMockMvc.standaloneSetup(mockMvcBuilder);

        when(songService.addSong(any())).thenReturn(1L);
    }
}
