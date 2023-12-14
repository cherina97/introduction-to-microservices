package com.example.songservice.service;

import com.example.songservice.model.Song;

import java.util.List;

public interface SongService {

    Long addSong(Song song);

    Song getSongById(Long id);

    List<Long> deleteSongsByIds(List<Long> ids);

    List<Long> getAllSongsIds();
}
