package com.aendyear.komawatsir.entity;

import com.aendyear.komawatsir.type.PostStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "sender_id", nullable = false)
    private Integer senderId;

    @Column(length = 50)
    private String senderNickname;

    @Column(name = "receiver_id", nullable = false)
    private Integer receiverId;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PostStatus status;

    @Column(name = "image_url", nullable = true)
    private String imageUrl;

    private String year;
}
