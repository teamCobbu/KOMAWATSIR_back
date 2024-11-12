package com.aendyear.komawatsir.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InquiryDto {
    private Integer id;

    private Integer userId;

    private String year;
}
