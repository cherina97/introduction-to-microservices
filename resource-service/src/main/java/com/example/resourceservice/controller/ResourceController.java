package com.example.resourceservice.controller;

import com.example.resourceservice.service.ResourceService;
import com.example.resourceservice.service.S3Service;
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
    private final S3Service s3Service;

    @Autowired
    public ResourceController(ResourceService resourceService, S3Service s3Service) {
        this.resourceService = resourceService;
        this.s3Service = s3Service;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> uploadNewResource(@RequestParam("file") MultipartFile data) throws IOException, TikaException, SAXException {
        Long id = resourceService.uploadNewResource(data);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getResourceById(@PathVariable long id) {
        String key = resourceService.getResourceKeyById(id);
        return new ResponseEntity<>(key, HttpStatus.OK);
    }

    @DeleteMapping("/{bucketName}")
    public List<Long> deleteResource(@RequestParam(value = "ids") List<Long> ids, @PathVariable String bucketName) {
        return resourceService.deleteResources(ids, bucketName);
    }

    @GetMapping()
    public ResponseEntity<List<Long>> getAllResources() {
        return new ResponseEntity<>(resourceService.getAllResourcesIds(), HttpStatus.OK);
    }

    @GetMapping("/s3/bucket/{bucketName}")
    public ResponseEntity<List<String>> getAllResourcesInStorage(@PathVariable String bucketName) {
        return new ResponseEntity<>(s3Service.getAllResourcesInBucket(bucketName), HttpStatus.OK);
    }

    @GetMapping("/s3/{id}")
    public ResponseEntity<?> getResourceFromStaging(@PathVariable String id) throws IOException, TikaException, SAXException {
        return new ResponseEntity<>(resourceService.getResourceFromStaging(id), HttpStatus.OK);
    }

    @GetMapping("/process/{id}")
    public ResponseEntity<?> moveResourceToPermanent(@PathVariable Long id) {
        return new ResponseEntity<>(resourceService.moveResourceToPermanent(id), HttpStatus.OK);
    }
}
