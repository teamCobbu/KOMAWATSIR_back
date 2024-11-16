package com.aendyear.komawatsir.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FontDto {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private String url;
}
