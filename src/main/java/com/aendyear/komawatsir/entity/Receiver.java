package com.aendyear.komawatsir.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Receiver {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Integer senderId;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Integer receiverUserId;

    @Column(length = 50)
    private String nickname;

    @Column(length = 20)
    private String tel;

    @Column(columnDefinition = "TEXT")
    private String memo;

    private String year;

    private Boolean isDeleted;
}
