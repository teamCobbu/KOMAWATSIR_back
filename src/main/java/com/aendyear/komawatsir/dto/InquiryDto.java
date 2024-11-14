package com.aendyear.komawatsir.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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