package com.example.resourceservice.service;

import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.List;

public interface ResourceService {

    Long uploadNewResource(MultipartFile file) throws IOException, TikaException, SAXException;

    String getResourceKeyById(Long id);

    List<Long> deleteResources(List<Long> ids, String bucketName);

    List<Long> getAllResourcesIds();

    byte[] getResourceFromStaging(String resourceId) throws IOException, TikaException, SAXException;

    Long moveResourceToPermanent(Long id);

}
