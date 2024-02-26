package com.example.resourceservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.resourceservice.model.StorageType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class S3ServiceImpl implements S3Service {

    @Value("${s3.bucket}")
    private String bucketName;
    private final AmazonS3 amazonS3;
    private final StorageService storageService;

    @Autowired
    public S3ServiceImpl(AmazonS3 amazonS3, StorageService storageService) {
        this.amazonS3 = amazonS3;
        this.storageService = storageService;
    }

    @Override
    public String addResource(MultipartFile file) throws IOException {
        return getBucketOfSavedFile(file, bucketName);
    }

    @Override
    public String addResourceToStaging(MultipartFile file) throws IOException {
        String stagingBucket = storageService.getStorageByType(StorageType.STAGING).getBucket();
        amazonS3.createBucket(stagingBucket);
        return getBucketOfSavedFile(file, stagingBucket);
    }

    private String getBucketOfSavedFile(MultipartFile file, String bucketName) throws IOException {
        String key = file.getOriginalFilename();
        InputStream inputStream;

        inputStream = file.getInputStream();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(bucketName, key, inputStream, metadata);

        return bucketName;
    }

    @Override
    public void moveResourceToPermanent(String key) {
        String permBucket = storageService.getStorageByType(StorageType.PERMANENT).getBucket();
        String stagingBucket = storageService.getStorageByType(StorageType.STAGING).getBucket();

        amazonS3.createBucket(permBucket);
        amazonS3.copyObject(stagingBucket, key, permBucket, key);
        amazonS3.deleteObject(stagingBucket, key);
    }

    @Override
    public byte[] getResource(String key, String bucket) throws IOException {
        S3Object amazonS3Object = amazonS3.getObject(bucket, key);

        return amazonS3Object.getObjectContent().readAllBytes();
    }

    @Override
    public void deleteResources(List<String> keys, String bucket) {
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket)
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
