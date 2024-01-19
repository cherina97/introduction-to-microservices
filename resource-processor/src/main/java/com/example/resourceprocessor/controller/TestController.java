package com.example.resourceprocessor.controller;

import com.example.resourceprocessor.service.ProcessorServiceImpl;
import org.apache.tika.exception.TikaException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import java.io.IOException;

@RestController
@RequestMapping("/test")
public class TestController {

    private final ProcessorServiceImpl processorService;

    public TestController(ProcessorServiceImpl processorService) {
        this.processorService = processorService;
    }

    @PostMapping("/{id}")
    public ResponseEntity<?> testCallingServices(@PathVariable String id) throws TikaException, IOException, SAXException {

        processorService.consume(id);

        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
