package com.aendyear.komawatsir.dto;

import com.aendyear.komawatsir.entity.User;
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

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.tel = user.getTel();
        this.isSmsAllowed = user.getIsSmsAllowed();
    }

    public UserDto(User user, String accessToken) {
        this.id = user.getId();
        this.name = user.getName();
        this.kakaoId = user.getKakaoId();
        this.isSmsAllowed = user.getIsSmsAllowed();
    }
}
