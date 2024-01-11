package com.example.resourceprocessor.service;

import com.example.resourceprocessor.model.Song;
import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;

public interface ProcessorService {

    Song parseFile(MultipartFile file, Long resourceId) throws IOException, TikaException, SAXException;
}
