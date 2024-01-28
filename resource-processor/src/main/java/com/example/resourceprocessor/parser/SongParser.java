package com.example.resourceprocessor.parser;

import com.example.resourceprocessor.model.Song;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class SongParser {

    private static final String NAME = "dc:title";
    private static final String ARTIST = "xmpDM:artist";
    private static final String ALBUM = "xmpDM:album";
    private static final String DURATION = "xmpDM:duration";
    private static final String YEAR = "xmpDM:releaseDate";

    public Song parseBytes(byte[] file, Long resourceId) throws IOException, TikaException, SAXException {

        InputStream inputStream = new ByteArrayInputStream(file);

        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext parseContext = new ParseContext();
        Parser parser = new Mp3Parser();

        parser.parse(inputStream, handler, metadata, parseContext);

        return new Song(
                metadata.get(NAME),
                metadata.get(ARTIST),
                metadata.get(ALBUM),
                metadata.get(DURATION),
                metadata.get(YEAR),
                resourceId);
    }
}
