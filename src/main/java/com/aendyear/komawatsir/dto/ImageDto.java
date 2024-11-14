package com.aendyear.komawatsir.dto;

import com.aendyear.komawatsir.entity.Image;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageDto {

    private Integer id;

    @NotNull
    private String category;

    @NotNull
    private String name;

    @NotNull
    private String pic;

    @NotNull
    private Boolean isFront;

    private Image.SourceType sourceType;

    public enum SourceType {
        SERVICE, USER, THIRD_PARTY
    }
}
