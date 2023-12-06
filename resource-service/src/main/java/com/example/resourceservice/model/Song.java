package com.example.resourceservice.model;

import lombok.*;

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
