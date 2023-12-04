package com.example.songservice.service;

import com.example.songservice.exception.InvalidFileFormatException;
import com.example.songservice.exception.ResourceNotFoundException;
import com.example.songservice.model.Resource;
import com.example.songservice.parser.ResourceParser;
import com.example.songservice.repository.ResourceRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceParser resourceParser;

    @Autowired
    public ResourceServiceImpl(ResourceRepository resourceRepository, ResourceParser resourceParser) {
        this.resourceRepository = resourceRepository;
        this.resourceParser = resourceParser;
    }

    @Override
    public Long uploadNewResource(MultipartFile data) throws IOException {

        if (!Objects.equals(FilenameUtils.getExtension(data.getOriginalFilename()), "mp3")) {
            throw new InvalidFileFormatException("File should have .mp3 extension");
        }

        Resource resource = new Resource();
        resource.setData(data.getBytes());

        Resource savedResource = resourceRepository.save(resource);

        return savedResource.getId();
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
}
