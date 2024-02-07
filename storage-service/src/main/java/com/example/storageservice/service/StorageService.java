package com.example.storageservice.service;

import com.example.storageservice.model.StorageObject;

import java.util.List;

public interface StorageService {

    Long addStorage(StorageObject storageObject);

    StorageObject getStorageById(Long id);

    List<Long> deleteStoragesByIds(List<Long> ids);

    List<StorageObject> getAllStorages();

}
