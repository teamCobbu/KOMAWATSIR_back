package com.aendyear.komawatsir.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InquiryDto {

    private Integer id;

    @NotNull
    private Integer userId;

    private String year;

    @NotNull
    private String nickname;
}