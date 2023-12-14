package com.example.songservice.service;

import com.example.songservice.exception.SongAlreadyExistsException;
import com.example.songservice.exception.SongNotFoundException;
import com.example.songservice.model.Song;
import com.example.songservice.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    @Autowired
    public SongServiceImpl(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public Long addSong(Song song) {
        checkIfSongExists(song);
        return songRepository.save(song).getId();
    }

    private void checkIfSongExists(Song song) {
        List<Song> allSongs = new ArrayList<>();
        songRepository.findAll().iterator().forEachRemaining(allSongs::add);

        allSongs.stream()
                .filter(existingSong -> song.getName().equals(existingSong.getName()))
                .findFirst()
                .ifPresent(s -> {
                    throw new SongAlreadyExistsException("Song already exist with name = " + s.getName());
                });
    }

    @Override
    public Song getSongById(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new SongNotFoundException("Not found song by id = " + id));
    }

    @Override
    public List<Long> deleteSongsByIds(List<Long> ids) {
        List<Song> songsToDelete = new ArrayList<>();

        for (Long id : ids) {
            Song songById = songRepository.findById(id)
                    .orElseThrow(() -> new SongNotFoundException("Not found song by id = " + id));
            songsToDelete.add(songById);
        }

        songRepository.deleteAll(songsToDelete);
        return songsToDelete.stream().map(Song::getId).collect(Collectors.toList());
    }

    @Override
    public List<Long> getAllSongsIds() {
        List<Song> allSongs = new ArrayList<>();
        songRepository.findAll().iterator().forEachRemaining(allSongs::add);

        return allSongs.stream().map(Song::getId).collect(Collectors.toList());
    }
}
