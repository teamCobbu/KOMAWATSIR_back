package com.aendyear.komawatsir.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InquiryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "inquiry_id", nullable = false)
    private Integer inquiryId;

    @Column
    private String question;

    @Column
    private String description;
}
