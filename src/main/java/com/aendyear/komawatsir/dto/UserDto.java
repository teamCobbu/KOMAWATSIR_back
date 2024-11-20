package com.aendyear.komawatsir.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {
    private Integer id;
    private String name;
    @Size(min = 10, max = 11)
    private String tel;
    private String kakaoId;
    private Boolean isSmsAllowed;
}
