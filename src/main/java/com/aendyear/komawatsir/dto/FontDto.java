package com.aendyear.komawatsir.dto;

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

    private String color;
}
