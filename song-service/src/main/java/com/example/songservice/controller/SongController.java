package com.example.songservice.controller;

import com.example.songservice.model.Song;
import com.example.songservice.service.SongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/songs")
public class SongController {

    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    //todo add ExceptionHandler
    @PostMapping()
    public ResponseEntity<Long> uploadNewResource(@RequestBody Song song) {

        Long id = songService.addSong(song);

        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> getResourceById(@PathVariable long id) {

        Song songById = songService.getSongById(id);

        return new ResponseEntity<>(songById, HttpStatus.OK);
    }

    @DeleteMapping
    public List<Long> deleteUser(@RequestParam(value = "ids") List<Long> ids) {

        return songService.deleteSongsByIds(ids);
    }
}
