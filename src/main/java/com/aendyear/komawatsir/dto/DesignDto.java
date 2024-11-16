package com.aendyear.komawatsir.dto;

import com.aendyear.komawatsir.type.FontColor;
import com.aendyear.komawatsir.type.FontSize;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DesignDto {
    private Integer id;

    private Integer userId;

    private Integer backgroundId;

    private Integer thumbnailId;

    private Integer fontId;

    private String year;

    private FontSize fontSize;

    private FontColor fontColor;
}
