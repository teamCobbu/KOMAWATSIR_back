package com.aendyear.komawatsir.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Design {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "background_id")
    private Image background;

    @ManyToOne
    @JoinColumn(name = "thumbnail_id")
    private Image thumbnail;

    @ManyToOne
    @JoinColumn(name = "font_id")
    private Font font;

    private Integer year;
}
