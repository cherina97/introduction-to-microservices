package com.example.storageservice.service;

import com.example.storageservice.model.StorageObject;
import com.example.storageservice.repository.StorageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StorageServiceImpl implements StorageService {

    private final StorageRepository storageRepository;

    public StorageServiceImpl(StorageRepository storageRepository) {
        this.storageRepository = storageRepository;
    }

    @Override
    public Long addStorage(StorageObject storageObject) {
        return storageRepository.save(storageObject).getId();
    }

    @Override
    public StorageObject getStorageById(Long id) {
        return storageRepository.findById(id).orElse(null);
    }

    @Override
    public List<Long> deleteStoragesByIds(List<Long> ids) {
        List<StorageObject> storagesToDelete = new ArrayList<>();

        for (Long id : ids) {
            StorageObject storageObjectById = storageRepository.findById(id).orElse(null);
            storagesToDelete.add(storageObjectById);
        }

        storageRepository.deleteAll(storagesToDelete);
        return storagesToDelete.stream().map(StorageObject::getId).collect(Collectors.toList());
    }

    @Override
    public List<StorageObject> getAllStorages() {
        List<StorageObject> storageObjects = new ArrayList<>();
        storageRepository.findAll().iterator().forEachRemaining(storageObjects::add);
        return storageObjects;
    }
}
