package com.aendyear.komawatsir.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InquiryItemDto {
    private Integer id;

    private Integer inquiryId;

    private String question;

    private String description;
}
