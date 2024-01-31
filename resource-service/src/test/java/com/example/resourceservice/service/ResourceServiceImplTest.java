package com.example.resourceservice.service;

import com.example.resourceservice.config.AWSConfig;
import com.example.resourceservice.model.Resource;
import com.example.resourceservice.repository.ResourceRepository;
import org.apache.tika.exception.TikaException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;

class ResourceServiceImplTest {

    @Mock
    private ResourceRepository resourceRepository;
    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private S3Service s3Service;
    @Mock
    private Resource resource;
    @Mock
    AWSConfig awsConfig;

    private MockMultipartFile file;
    private ResourceService resourceService;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        this.resourceService = new ResourceServiceImpl(resourceRepository, s3Service, kafkaTemplate);
        Mockito.when(resource.getId()).thenReturn(1L);

        this.file = new MockMultipartFile("file.mp3", "file.mp3", null, "bar".getBytes());
    }

    @Test
    public void addResourceTest() throws IOException, TikaException, SAXException {
        String bucketName = "resources";

        Mockito.when(s3Service.addResource(Mockito.any())).thenReturn(bucketName);
        Mockito.when(resourceRepository.save(Mockito.any())).thenReturn(resource);

        Long resourceId = resourceService.uploadNewResource(file);

        Assertions.assertEquals(1L, resourceId);
    }
}