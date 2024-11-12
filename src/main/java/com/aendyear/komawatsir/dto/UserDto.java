package com.aendyear.komawatsir.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Integer id;

    private String name;

    private String tel;

    private String kakaoId;

    private Boolean isSmsAllowed;
}
