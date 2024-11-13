package com.aendyear.komawatsir.dto;

import com.aendyear.komawatsir.type.FontColor;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FontDto {
    private Integer id;

    private String name;

    private Integer size;

    private String url;

    private FontColor color;

}
