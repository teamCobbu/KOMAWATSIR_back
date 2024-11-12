package com.aendyear.komawatsir.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DraftDto {
    private Integer id;

    private Integer userId;

    private String title;

    private String contents;
}
