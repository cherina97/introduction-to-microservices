package com.example.resourceservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.resourceservice.model.StorageObject;
import com.example.resourceservice.model.StorageType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
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
    @Value("${url.call.storages}")
    private String callStorages;
    private final AmazonS3 amazonS3;
    private final RestClient restClient;

    @Autowired
    public S3ServiceImpl(AmazonS3 amazonS3, RestClient restClient) {
        this.amazonS3 = amazonS3;
        this.restClient = restClient;
    }

    @Override
    public String addResource(MultipartFile file) {
        return getBucketOfSavedFile(file, bucketName);
    }

    @Override
    @CircuitBreaker(name = "StorageService", fallbackMethod = "getStagingStorageDefault")
    public String addResourceToStaging(MultipartFile file) {
        String stagingBucket = getStagingStorage().getBucket();
        amazonS3.createBucket(stagingBucket);

        return getBucketOfSavedFile(file, stagingBucket);
    }

    public StorageObject getStagingStorage() {
        return restClient.get()
                .uri(callStorages + "/1")
                .retrieve()
                .body(StorageObject.class);
    }

    public String getStagingStorageDefault(MultipartFile file, Throwable ex) {
        log.warn("Calling Circuit Breaker fallback Method");
        log.warn("Fallback Method with exception " + ex);

        StorageObject storage = new StorageObject(1L, StorageType.STAGING.name(), "staging", "/staging");
        amazonS3.createBucket(storage.getBucket());
        return getBucketOfSavedFile(file, storage.getBucket());
    }

    private String getBucketOfSavedFile(MultipartFile file, String bucketName) {
        String key = file.getOriginalFilename();
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(bucketName, key, inputStream, metadata);

        return bucketName;
    }

    @CircuitBreaker(name = "StorageService", fallbackMethod = "getPermStorageDefault")
    @Override
    public void moveResourceToPermanent(String key) {
        List<StorageObject> storages = getStorages();

        String permBucket = storages.stream()
                .filter(st -> st.getStorageType().equals(StorageType.PERMANENT.name()))
                .findFirst()
                .orElse(null)
                .getBucket();

        String stagingBucket = storages.stream()
                .filter(st -> st.getStorageType().equals(StorageType.STAGING.name()))
                .findFirst()
                .orElse(null)
                .getBucket();

        amazonS3.createBucket(permBucket);
        amazonS3.copyObject(stagingBucket, key, permBucket, key);
        amazonS3.deleteObject(stagingBucket, key);
    }

    public List<StorageObject> getStorages() {
        return restClient.get()
                .uri(callStorages)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public void getPermStorageDefault(String key, Throwable ex) {
        log.warn("Calling Circuit Breaker fallback Method");
        log.warn("Fallback Method with exception " + ex);

        StorageObject storage = new StorageObject(2L, StorageType.PERMANENT.name(), "permanent", "/permanent");
        String bucket = storage.getBucket();

        amazonS3.createBucket(bucket);
        amazonS3.copyObject("staging", key, bucket, key);
        amazonS3.deleteObject("staging", key);
    }

    @Override
    public byte[] getResource(String key) throws IOException {
        S3Object amazonS3Object = amazonS3.getObject("staging", key);

        return amazonS3Object.getObjectContent().readAllBytes();
    }

    @Override
    public void deleteResources(List<String> keys) {
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest("staging")
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
