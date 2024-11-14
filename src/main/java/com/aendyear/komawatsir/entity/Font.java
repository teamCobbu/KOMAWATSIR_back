package com.aendyear.komawatsir.entity;

import com.aendyear.komawatsir.type.FontColor;
import com.aendyear.komawatsir.type.FontSize;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Font {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    private FontSize size;

    @Column(length = 255)
    private String url;

    @Enumerated(EnumType.STRING)
    private FontColor color;


}

