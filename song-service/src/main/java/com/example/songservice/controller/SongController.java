package com.example.songservice.controller;

import com.example.songservice.model.Song;
import com.example.songservice.service.SongService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;

    @Autowired
    public SongController(SongService songService) {
        this.songService = songService;
    }

    @PostMapping
    public ResponseEntity<Long> uploadNewSong(@Valid @RequestBody Song song) {

        Long id = songService.addSong(song);

        return new ResponseEntity<>(id, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable long id) {

        Song songById = songService.getSongById(id);

        return new ResponseEntity<>(songById, HttpStatus.OK);
    }

    @DeleteMapping
    public List<Long> deleteSong(@RequestParam(value = "ids") List<Long> ids) {

        return songService.deleteSongsByIds(ids);
    }

    @GetMapping()
    public ResponseEntity<List<Long>> getAllSongs() {
        return new ResponseEntity<>(songService.getAllSongsIds(), HttpStatus.OK);
    }
}
