package com.aendyear.komawatsir.dto;

import com.aendyear.komawatsir.type.FontColor;
import com.aendyear.komawatsir.type.FontSize;
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
    private FontSize size;

    @NotNull
    private String url;

    private FontColor color;

}
