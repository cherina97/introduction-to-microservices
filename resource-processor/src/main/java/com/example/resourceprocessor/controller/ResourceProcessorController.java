package com.example.resourceprocessor.controller;

import com.example.resourceprocessor.service.ProcessorServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/processor")
public class ResourceProcessorController {

    private final ProcessorServiceImpl processorService;

    public ResourceProcessorController(ProcessorServiceImpl processorService) {
        this.processorService = processorService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> callService(@PathVariable String id) {

        byte[] bytes = processorService.callResourceService(id);

        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }
}
