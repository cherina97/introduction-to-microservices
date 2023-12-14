package com.example.resourceservice.controller;

import com.example.resourceservice.service.ResourceService;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

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
    public ResponseEntity<Long> uploadNewResource(@RequestParam("file") MultipartFile data) throws IOException, TikaException, SAXException {

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

    @GetMapping()
    public ResponseEntity<List<Long>> getAllResources() {
        return new ResponseEntity<>(resourceService.getAllResourcesIds(), HttpStatus.OK);
    }
}
