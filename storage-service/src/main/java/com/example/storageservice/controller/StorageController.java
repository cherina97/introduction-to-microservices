package com.example.storageservice.controller;


import com.example.storageservice.model.StorageObject;
import com.example.storageservice.service.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/storages")
public class StorageController {

    private final StorageService storageService;

    public StorageController(StorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping
    public ResponseEntity<Long> addStorage(@RequestBody StorageObject storageObject) {
        Long id = storageService.addStorage(storageObject);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StorageObject> getStorageById(@PathVariable long id) {
        StorageObject storageById = storageService.getStorageById(id);
        return new ResponseEntity<>(storageById, HttpStatus.OK);
    }

    @DeleteMapping
    public List<Long> deleteStorages(@RequestParam(value = "ids") List<Long> ids) {
        return storageService.deleteStoragesByIds(ids);
    }

    @GetMapping()
    public ResponseEntity<List<StorageObject>> getAllStorages() {
        return new ResponseEntity<>(storageService.getAllStorages(), HttpStatus.OK);
    }
}
