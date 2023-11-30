package com.example.model;

import lombok.Data;

@Data
//@NoArgsConstructor(force = true)
public class Resource {

    private Long id;
    private byte[] data;

}
