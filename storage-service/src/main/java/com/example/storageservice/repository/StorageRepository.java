package com.example.storageservice.repository;

import com.example.storageservice.model.StorageObject;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StorageRepository extends CrudRepository<StorageObject, Long> {
}
