package com.example.resourceservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "resource")
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @Column
    private byte[] data;

}
