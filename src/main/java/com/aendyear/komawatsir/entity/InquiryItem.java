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
public class InquiryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "inquiryId cannot be null")
    @Column(name = "inquiry_id", nullable = false)
    private Integer inquiryId;

    @NotNull(message = "question cannot be null")
    @Column
    private String question;

    @Column
    private String description;
}
