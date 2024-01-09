package com.example.resourceservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3ServiceImpl implements S3Service {

    //todo move to .env
    private static final String bucketName = "songs";

    private final AmazonS3 amazonS3;

    @Autowired
    public S3ServiceImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
//        initBucket();
    }

//    private void initBucket() {
//        if (!amazonS3.doesBucketExistV2(S3ServiceImpl.bucketName)) {
//            amazonS3.createBucket(S3ServiceImpl.bucketName);
//        }
//    }

    @Override
    public String addResource(MultipartFile file) throws IOException {

        amazonS3.createBucket(S3ServiceImpl.bucketName);

        String key = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(bucketName, key, inputStream, metadata);

        return key;
    }

    @Override
    public byte[] getResourceById(String key) throws IOException {
        return amazonS3.getObject(bucketName, key).getObjectContent().readAllBytes();
    }

    @Override
    public List<String> deleteResources(List<String> keys) {
        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucketName)
                .withKeys(keys.toArray(String[]::new));

        return amazonS3.deleteObjects(deleteObjectsRequest)
                .getDeletedObjects().stream()
                .map(DeleteObjectsResult.DeletedObject::getKey)
                .collect(Collectors.toList());
    }
}
