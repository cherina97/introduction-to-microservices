package com.example.resourceservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3ServiceImpl implements S3Service {

    @Value("${s3.bucket}")
    private String bucketName;
    private final AmazonS3 amazonS3;

    @Autowired
    public S3ServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
    }

    @Override
    public String addResource(MultipartFile file) throws IOException {
        String key = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(bucketName, key, inputStream, metadata);

        return bucketName;
    }

    @Override
    public byte[] getResource(String key) throws IOException {
        S3Object amazonS3Object = amazonS3.getObject(bucketName, key);

        return amazonS3Object.getObjectContent().readAllBytes();
    }

    @Override
    public void deleteResources(List<String> keys) {
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName)
                .withKeys(keys.toArray(String[]::new));

        amazonS3.deleteObjects(deleteObjectsRequest);
    }

    @Override
    public List<String> getAllResourcesInBucket(String bucketName) {
        return amazonS3.listObjects(bucketName).getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }
}
