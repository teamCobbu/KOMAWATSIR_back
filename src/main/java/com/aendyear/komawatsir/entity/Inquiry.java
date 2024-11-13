package com.aendyear.komawatsir.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "userId cannot be null")
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @NotNull(message = "year cannot be null")
    @Column(length = 4)
    private String year;

    private String nickname;
}
