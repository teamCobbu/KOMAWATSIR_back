package com.aendyear.komawatsir.auth;

import com.aendyear.komawatsir.dto.UserDto;
import com.aendyear.komawatsir.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final UserDto userDto;

    public CustomUserDetails(UserDto userDto) {
        this.userDto = userDto;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // GrantedAuthority 권한 설정 (예: ROLE_USER)
        return List.of(() -> "ROLE_USER");
    }

    @Override
    public String getPassword() {
        return "";  // 카카오는 패스워드를 사용하지 않음
    }

    @Override
    public String getUsername() {
        return userDto.getKakaoId();  // 사용자 이름대신 kakaoId 반환
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // 만료되지 않음
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // 잠기지 않음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // 자격 증명 만료되지 않음
    }

    @Override
    public boolean isEnabled() {
        return true;  // 활성화됨
    }

    public UserDto getKakao() {// 카카오 사용자 정보 반환
        return userDto;
    }
}