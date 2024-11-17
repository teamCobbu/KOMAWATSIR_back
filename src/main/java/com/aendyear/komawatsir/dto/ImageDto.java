package com.aendyear.komawatsir.dto;

import com.aendyear.komawatsir.type.ImageCategory;
import com.aendyear.komawatsir.type.SourceType;
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
    private ImageCategory category;

    @NotNull
    private String name;

    @NotNull
    private String pic;

    @NotNull
    private Boolean isFront;

    private SourceType sourceType;

    private Integer userId;
}
