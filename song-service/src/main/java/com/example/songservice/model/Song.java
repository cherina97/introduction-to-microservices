package com.example.songservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "song")
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NotBlank(message = "Name is mandatory")
    private String name;

    @Column
    private String artist;

    @Column
    private String album;

    @Column
    private String duration;

    @Column
    private String year;

    @Column(name = "resource_id")
    private Long resourceId;
}
