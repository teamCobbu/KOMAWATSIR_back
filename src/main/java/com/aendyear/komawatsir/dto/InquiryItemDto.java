package com.aendyear.komawatsir.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InquiryItemDto {

    private Integer id;

    @NotNull
    private Integer inquiryId;

    @NotNull
    @Size(min = 1, max = 100)
    private String question;

    @Size(min = 1, max = 100)
    private String description;
}
