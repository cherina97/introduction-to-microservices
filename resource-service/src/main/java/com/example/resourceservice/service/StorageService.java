package com.example.resourceservice.service;

import com.example.resourceservice.model.StorageObject;
import com.example.resourceservice.model.StorageType;

public interface StorageService {

    StorageObject getStorageByType(StorageType storageType);
}
