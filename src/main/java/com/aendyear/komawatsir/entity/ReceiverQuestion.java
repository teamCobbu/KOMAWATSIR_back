package com.aendyear.komawatsir.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiverQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "inquiry_item_id", nullable = false)
    private Integer inquiryItemId;

    @Column(name = "receiver_id", nullable = false)
    private Integer receiverId;

    private String answer;
}
