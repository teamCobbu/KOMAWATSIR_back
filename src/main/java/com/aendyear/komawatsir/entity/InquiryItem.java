package com.aendyear.komawatsir.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class InquiryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    @Column
    private String question;

    @Column
    private String description;
}
