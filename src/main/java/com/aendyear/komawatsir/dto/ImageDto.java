package com.aendyear.komawatsir.dto;

import com.aendyear.komawatsir.type.ImageCategory;
import com.aendyear.komawatsir.type.SourceType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Enumerated(EnumType.STRING)
    private ImageCategory category;

    @NotNull
    private String name;

    @NotNull
    private String pic;

    @NotNull
    private Boolean isFront ;

    @Enumerated(EnumType.STRING)
    private SourceType sourceType;

    private Integer userId;
}
