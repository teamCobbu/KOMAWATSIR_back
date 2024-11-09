package com.aendyear.komawatsir.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String category;

    @Column(length = 100)
    private String name;

    @Lob
    private byte[] pic;

    private Boolean isFront;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SourceType sourceType;

    public enum SourceType {
        SERVICE, USER, THIRD_PARTY
    }
}

