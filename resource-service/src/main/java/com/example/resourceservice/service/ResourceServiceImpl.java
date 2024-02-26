package com.example.resourceservice.service;

import com.example.resourceservice.exception.InvalidFileFormatException;
import com.example.resourceservice.exception.ResourceNotFoundException;
import com.example.resourceservice.model.Resource;
import com.example.resourceservice.repository.ResourceRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final S3Service s3Service;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${kafka.topic}")
    private String topic;

    @Autowired
    public ResourceServiceImpl(ResourceRepository resourceRepository, S3Service s3Service, KafkaTemplate<String, String> kafkaTemplate) {
        this.resourceRepository = resourceRepository;
        this.s3Service = s3Service;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public Long uploadNewResource(MultipartFile file) throws IOException {

        if (!Objects.equals(FilenameUtils.getExtension(file.getOriginalFilename()), "mp3")) {
            throw new InvalidFileFormatException("File should have .mp3 extension");
        }

        //save the source file to a cloud storage STAGING
        String bucketName = s3Service.addResourceToStaging(file);
        log.info("Mowing file to STAGING bucket");

        Resource resource = new Resource();
        resource.setBucket(bucketName);
        resource.setResourceKey(file.getOriginalFilename());

        //save resource location (bucket + name)
        Resource savedResource = resourceRepository.save(resource);
        log.info("Saving file to DB");

        //pass message to the topic
        kafkaTemplate.send(topic, savedResource.getId().toString());
        log.info("[Resource Service] Pissing message to the topic " + topic);

        return savedResource.getId();
    }

    @Override
    public Long moveResourceToPermanent(Long resourceId) {
        String resourceKey = resourceRepository.findById(resourceId)
                .map(Resource::getResourceKey)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found by id = " + resourceId));

        log.info("Mowing resource with id " + resourceId + ", key: " + resourceKey + " to PERMANENT bucket");
        s3Service.moveResourceToPermanent(resourceKey);

        return resourceId;
    }

    @Override
    public String getResourceKeyById(Long id) {
        return resourceRepository.findById(id)
                .map(Resource::getResourceKey)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found by id = " + id));
    }

    @Override
    public byte[] getResourceFromStaging(String resourceId) throws IOException {
        long id = Long.parseLong(resourceId);

        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found by id = " + id));

        return s3Service.getResource(resource.getResourceKey(), "staging");
    }

    @Override
    public List<Long> deleteResources(List<Long> ids, String bucketName) {
        List<Resource> resourcesToDelete = new ArrayList<>();

        for (Long id : ids) {
            resourceRepository.findById(id).ifPresent(resourcesToDelete::add);
        }

        resourceRepository.deleteAll(resourcesToDelete);

        List<String> keyToDelete = resourcesToDelete.stream().map(Resource::getResourceKey).toList();
        s3Service.deleteResources(keyToDelete, bucketName);

        return resourcesToDelete.stream().map(Resource::getId).collect(Collectors.toList());
    }

    @Override
    public List<Long> getAllResourcesIds() {
        List<Resource> allResources = new ArrayList<>();
        resourceRepository.findAll().iterator().forEachRemaining(allResources::add);

        return allResources.stream().map(Resource::getId).collect(Collectors.toList());
    }
}
