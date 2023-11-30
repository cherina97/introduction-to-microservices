package com.example.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ResourceService {

    Long uploadNewResource(MultipartFile data) throws IOException;

    byte[] getResourceData(Long id);

    List<Long> deleteResources(List<Long> ids);
}
