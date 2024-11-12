package com.aendyear.komawatsir.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 20)
    private String tel;

    @Column(nullable = false, length = 100)
    private String kakaoId;

    @Column(name = "is_sms_allowed")
    private Boolean isSmsAllowed;
}
