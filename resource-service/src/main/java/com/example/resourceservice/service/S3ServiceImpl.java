package com.example.resourceservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.example.resourceservice.model.StorageObject;
import com.example.resourceservice.model.StorageType;
import com.netflix.discovery.EurekaClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    private String stagingBucket;
    private String permanentBucket;

    @Autowired
    public S3ServiceImpl(AmazonS3 amazonS3, RestClient restClient) {
        this.amazonS3 = amazonS3;
        this.restClient = restClient;
    }

    @Override
    public String addResource(MultipartFile file) throws IOException {
        return getBucketOfSavedFile(file, bucketName);
    }

    @Override
    public String addResourceToStaging(MultipartFile file) throws IOException {
        validateCreatedBuckets();
        return getBucketOfSavedFile(file, stagingBucket);
    }

    private String getBucketOfSavedFile(MultipartFile file, String bucketName) throws IOException {
        String key = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(bucketName, key, inputStream, metadata);

        return bucketName;
    }

    @Override
    public void moveResourceToPermanent(String key) {
        amazonS3.copyObject(stagingBucket, key, permanentBucket, key);
        amazonS3.deleteObject(stagingBucket, key);
    }

    @Override
    public byte[] getResource(String key) throws IOException {
        S3Object amazonS3Object = amazonS3.getObject(stagingBucket, key);

        return amazonS3Object.getObjectContent().readAllBytes();
    }

    @Override
    public void deleteResources(List<String> keys) {
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(stagingBucket)
                .withKeys(keys.toArray(String[]::new));

        amazonS3.deleteObjects(deleteObjectsRequest);
    }

    @Override
    public List<String> getAllResourcesInBucket(String bucketName) {
        return amazonS3.listObjects(bucketName).getObjectSummaries().stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.toList());
    }

    public void validateCreatedBuckets() {
        List<StorageObject> storages = getStorages();

        Optional<StorageObject> staging = Optional.ofNullable(storages).orElse(Collections.emptyList()).stream()
                .filter(storage -> storage.getBucket().equals(StorageType.STAGING.name()))
                .findFirst();

        Optional<StorageObject> permanent = Optional.ofNullable(storages).orElse(Collections.emptyList()).stream()
                .filter(storage -> storage.getBucket().equals(StorageType.PERMANENT.name()))
                .findFirst();

        createStagingBucket(staging);
        createPermanentBucket(permanent);
    }

    public void createStagingBucket(Optional<StorageObject> staging) {
        if (staging.isPresent()) {
            amazonS3.createBucket(staging.get().getBucket());
            this.stagingBucket = staging.get().getBucket();
        } else {
            amazonS3.createBucket("staging");
            this.stagingBucket = "staging";
        }
    }

    public void createPermanentBucket(Optional<StorageObject> permanent) {
        if (permanent.isPresent()) {
            amazonS3.createBucket(permanent.get().getBucket());
            this.permanentBucket = permanent.get().getBucket();
        } else {
            amazonS3.createBucket("permanent");
            this.permanentBucket = "permanent";
        }
    }

//    private String getStorageServiceUrl() {
//        return eurekaClient.getNextServerFromEureka("gateway-service", false).getHomePageUrl() + "storages";
//    }

    public List<StorageObject> getStorages() {
        return restClient.get()
                .uri(callStorages)
                .retrieve()
                .body(new ParameterizedTypeReference<List<StorageObject>>() {
                });
    }
}
