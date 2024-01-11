package com.example.resourceprocessor.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Song {

    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;
    private Long resourceId;
}
