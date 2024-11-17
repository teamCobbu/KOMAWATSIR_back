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
    private String name;// 카카오에서 닉네임으로 설정

    @Column(length = 20)
    private String tel;

    @Column(nullable = false, length = 100)
    private String kakaoId;// 카카오 사용자 ID

    @Column(name = "is_sms_allowed", nullable = false, columnDefinition = "boolean default false")
    private Boolean isSmsAllowed;
}
