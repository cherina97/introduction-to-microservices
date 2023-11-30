package com.example.service;

import com.example.model.Song;
import com.example.repository.SongRepository;
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
        return songRepository.save(song).getId();
    }

    @Override
    public Song getSongById(Long id) {
        return songRepository.findById(id).orElse(null);
    }

    @Override
    public List<Long> deleteSongsByIds(List<Long> ids) {
        List<Song> songsToDelete = new ArrayList<>();

        for (Long id : ids) {
            songRepository.findById(id).ifPresent(songsToDelete::add);
        }

        songRepository.deleteAll(songsToDelete);

        return songsToDelete.stream().map(Song::getId).collect(Collectors.toList());
    }
}
