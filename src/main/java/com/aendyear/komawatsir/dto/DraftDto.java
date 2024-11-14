package com.aendyear.komawatsir.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DraftDto {
    private Integer id;

    @NotNull
    private Integer userId;

    @NotNull
    private String title;

    @NotNull
    private String contents;
}
