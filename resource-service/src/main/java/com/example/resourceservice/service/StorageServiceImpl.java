package com.example.resourceservice.service;

import com.example.resourceservice.model.StorageObject;
import com.example.resourceservice.model.StorageType;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class StorageServiceImpl implements StorageService {

    @Value("${url.call.storages}")
    private String callStorages;
    private final RestClient restClient;

    public StorageServiceImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    @CircuitBreaker(name = "StorageService", fallbackMethod = "getStorageByTypeDefault")
    public StorageObject getStorageByType(StorageType storageType) {
        List<StorageObject> storages = restClient.get()
                .uri(callStorages)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return Objects.requireNonNull(storages).stream()
                .filter(storage -> storage.getStorageType().equals(storageType.name()))
                .findFirst()
                .orElse(null);
    }

    public StorageObject getStorageByTypeDefault(StorageType storageType, Throwable ex) {
        log.warn("Calling Circuit Breaker fallback Method");
        log.warn("Fallback Method with exception " + ex);

        StorageObject staging =
                new StorageObject(1L, StorageType.STAGING.name(), "staging", "/staging");
        StorageObject permanent =
                new StorageObject(2L, StorageType.PERMANENT.name(), "permanent", "/permanent");

        return storageType.equals(StorageType.STAGING)
                ? staging
                : permanent;
    }
}

