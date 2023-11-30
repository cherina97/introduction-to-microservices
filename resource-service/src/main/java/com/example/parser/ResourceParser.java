package com.example.parser;

import com.example.model.Song;
import lombok.SneakyThrows;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Component
public class ResourceParser {

    private static final String NAME = "title";
    private static final String ARTIST = "xmpDM:artist";
    private static final String ALBUM = "xmpDM:album";
    private static final String DURATION = "xmpDM:duration";
    private static final String YEAR = "xmpDM:releaseDate";

    //todo remove annotation
    @SneakyThrows
    public Song parse(MultipartFile file, Long resourceId) {

        InputStream inputStream = file.getInputStream();

        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        ParseContext parseContext = new ParseContext();
        Parser parser = new Mp3Parser();

        parser.parse(inputStream, handler, metadata, parseContext);

        return Song.builder()
                .name(metadata.get(NAME))
                .artist(metadata.get(ARTIST))
                .album(metadata.get(ALBUM))
                .duration(metadata.get(DURATION))
                .year(metadata.get(YEAR))
                .resourceId(resourceId)
                .build();
    }
}
