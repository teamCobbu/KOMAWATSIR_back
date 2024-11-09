package com.aendyear.komawatsir.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @Column(length = 50)
    private String senderNickname;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Receiver receiver;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status;

    private Integer year;

    public enum Status {
        PENDING, PROGRESSING, COMPLETED, DELETED
    }

    @Column(name = "is_sms_allowed", nullable = false)
    private Boolean isSmsAllowed = false;
}
