package com.example.songservice.controller;

import com.example.songservice.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> uploadNewResource(@RequestParam("file") MultipartFile data) throws IOException {

        //todo Invoke Song Service to save mp3 file metadata
        Long id = resourceService.uploadNewResource(data);

        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResourceById(@PathVariable long id) {

        byte[] resourceData = resourceService.getResourceData(id);

        return new ResponseEntity<>(resourceData, HttpStatus.OK);
    }

    @DeleteMapping
    public List<Long> deleteResource(@RequestParam(value = "ids") List<Long> ids) {

        return resourceService.deleteResources(ids);
    }
}
