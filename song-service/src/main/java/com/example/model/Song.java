package com.example.model;

import lombok.Data;

@Data
public class Song {

    private Long id;
    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;
    private Long resourceId;
}
