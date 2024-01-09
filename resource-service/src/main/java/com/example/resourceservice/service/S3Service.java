package com.example.resourceservice.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface S3Service {

    String addResource(MultipartFile file) throws IOException;

    byte[] getResourceById(String key) throws IOException;

    List<String> deleteResources(List<String> ids);

}
