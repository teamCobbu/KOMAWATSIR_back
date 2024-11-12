package com.aendyear.komawatsir.dto;

import com.aendyear.komawatsir.entity.Image;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageDto {
    private Integer id;

    private String category;

    private String name;

    private String pic;

    private Boolean isFront;

    private Image.SourceType sourceType;

    public enum SourceType {
        SERVICE, USER, THIRD_PARTY
    }
}
