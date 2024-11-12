package com.aendyear.komawatsir.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Design {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "background_id")
    private Integer backgroundId;

    @Column(name = "thumbnail_id")
    private Integer thumbnailId;

    @Column(name = "font_id")
    private Integer fontId;

    private Integer year;
}
