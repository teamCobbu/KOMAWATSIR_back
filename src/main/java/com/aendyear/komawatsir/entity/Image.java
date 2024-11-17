package com.aendyear.komawatsir.entity;

import com.aendyear.komawatsir.type.ImageCategory;
import com.aendyear.komawatsir.type.SourceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private ImageCategory category;

    @Column(length = 100)
    private String name;

    private String pic;

    private Boolean isFront;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private SourceType sourceType;

    private Integer userId;
}

