package com.example.model;

import lombok.Builder;

@Builder
public class Song {

    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;
    private Long resourceId;
}
