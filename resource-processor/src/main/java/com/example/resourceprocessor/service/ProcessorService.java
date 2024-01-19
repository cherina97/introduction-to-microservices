package com.example.resourceprocessor.service;

import org.apache.tika.exception.TikaException;
import org.xml.sax.SAXException;

import java.io.IOException;

public interface ProcessorService {

    void consume(String resourceId) throws TikaException, IOException, SAXException;
}
