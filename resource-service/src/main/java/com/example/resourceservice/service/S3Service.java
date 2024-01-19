package com.example.resourceservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface S3Service {

    String addResource(MultipartFile file) throws IOException;

    void deleteResources(List<String> ids);

    List<String> getAllResourcesInBucket(String bucketName);

    byte[] getResource(String key) throws IOException;

}
