package com.aendyear.komawatsir.dto;

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

    private Integer year;
}
