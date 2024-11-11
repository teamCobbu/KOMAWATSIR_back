package com.aendyear.komawatsir.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ReceiverQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "inquiry_item_id", nullable = false)
    private InquiryItem inquiryItem;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private Receiver receiver;

    private String answer;

}
