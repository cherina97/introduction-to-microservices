package com.example.resourceservice.service;

import com.example.resourceservice.exception.InvalidFileFormatException;
import com.example.resourceservice.exception.ResourceNotFoundException;
import com.example.resourceservice.model.Resource;
import com.example.resourceservice.model.Song;
import com.example.resourceservice.parser.ResourceParser;
import com.example.resourceservice.repository.ResourceRepository;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceParser resourceParser;
    private final RestClient restClient;

    @Autowired
    public ResourceServiceImpl(ResourceRepository resourceRepository, ResourceParser resourceParser, RestClient restClient) {
        this.resourceRepository = resourceRepository;
        this.resourceParser = resourceParser;
        this.restClient = restClient;
    }

    @Override
    public Long uploadNewResource(MultipartFile data) throws IOException, TikaException, SAXException {

        if (!Objects.equals(FilenameUtils.getExtension(data.getOriginalFilename()), "mp3")) {
            throw new InvalidFileFormatException("File should have .mp3 extension");
        }

        Resource resource = new Resource();
        resource.setData(data.getBytes());

        Resource savedResource = resourceRepository.save(resource);

        callSongService(data, savedResource);

        return savedResource.getId();
    }

    private void callSongService(MultipartFile data, Resource savedResource) throws IOException, TikaException, SAXException {
        Song parsedSong = resourceParser.parse(data, savedResource.getId());
        restClient.post()
                .contentType(APPLICATION_JSON)
                .body(parsedSong)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public byte[] getResourceData(Long id) {
        return resourceRepository.findById(id).map(Resource::getData)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found by id = " + id));
    }

    @Override
    public List<Long> deleteResources(List<Long> ids) {
        List<Resource> resourcesToDelete = new ArrayList<>();

        for (Long id : ids) {
            resourceRepository.findById(id).ifPresent(resourcesToDelete::add);
        }

        resourceRepository.deleteAll(resourcesToDelete);

        return resourcesToDelete.stream().map(Resource::getId).collect(Collectors.toList());
    }

    @Override
    public List<Long> getAllResourcesIds() {
        List<Resource> allResources = new ArrayList<>();
        resourceRepository.findAll().iterator().forEachRemaining(allResources::add);

        return allResources.stream().map(Resource::getId).collect(Collectors.toList());
    }
}
