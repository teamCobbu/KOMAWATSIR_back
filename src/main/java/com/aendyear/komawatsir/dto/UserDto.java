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
    @Pattern(regexp = "^[A-Za-z가-힣]+$")
    private String name;
    @Pattern(regexp = "^(010|011)[0-9]{8}$", message = "전화번호는 11자리 숫자만 가능합니다.")
    private String tel;
    @Pattern(regexp = "^[0-9]{10,11}$")
    private String kakaoId;
    @NotNull
    private Boolean isSmsAllowed;

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.tel = user.getTel();
        this.isSmsAllowed = user.getIsSmsAllowed();
    }
}
