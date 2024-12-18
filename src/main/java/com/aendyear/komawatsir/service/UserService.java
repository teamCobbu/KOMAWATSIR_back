package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.auth.AuthService;
import com.aendyear.komawatsir.auth.JwtTokenProvider;
import com.aendyear.komawatsir.auth.KakaoAuthService;
import com.aendyear.komawatsir.auth.KakaoUserService;
import com.aendyear.komawatsir.dto.UserDto;
import com.aendyear.komawatsir.entity.User;
import com.aendyear.komawatsir.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final KakaoAuthService kakaoAuthService;
    private final KakaoUserService kakaoUserService;
    private final UserRepository userRepository;
    private final AuthService authService;

    @Autowired
    public UserService(KakaoAuthService kakaoAuthService, KakaoUserService kakaoUserService, UserRepository userRepository, AuthService authService) {
        this.kakaoAuthService = kakaoAuthService;
        this.kakaoUserService = kakaoUserService;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    // ****  회원 가입 및 로그인, 로그아웃  *****
    // 카카오 로그인
    public UserDto getKakaoLogin(String code, String clientId, String redirectUri, HttpServletRequest request, HttpServletResponse response) {
        String accessToken = kakaoAuthService.getAccessToken(code, clientId, redirectUri);
        if (accessToken == null) {
            throw new RuntimeException("Failed to retrieve access token.");
        }

        User user = findOrSaveUser(kakaoUserService.getKakaoUserInfo(accessToken));
        authService.addJwtToCookie(user.getKakaoId(), response);
        authService.addAccessTokenToSession(accessToken, request);

        return Mapper.toDto(user);
    }

    //인증된 사용자
    public Integer getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("인증되지 않은 사용자입니다.");
        }

        String kakaoId = authentication.getName();
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return user.getId();
    }

    // 사용자 정보를 데이터베이스에서 조회하거나, 없으면 새로 저장
    private User findOrSaveUser(User user) {
        return userRepository.findByKakaoId(user.getKakaoId())
                .orElseGet(() -> userRepository.save(user));
    }

    //로그아웃
    public boolean logout(String accessToken, HttpServletRequest request, HttpServletResponse response) {
        if (!kakaoAuthService.logout(accessToken)) return false;
        authService.invalidateSession(request);
        authService.deleteJwtCookie(response);
        return true;
    }

    // 비회원 -> 회원 연결
    @Transactional
    public UserDto signUpWithKakao(String kakaoId, String name, String tel) {

        User user = userRepository.findByTel(tel)
                .map(existingUser -> {
                    existingUser.setKakaoId(kakaoId);
                    existingUser.setName(name);
                    return existingUser;
                })
                .orElseGet(() -> User.builder()
                        .kakaoId(kakaoId)
                        .name(name)
                        .tel(tel)
                        .isSmsAllowed(false)
                        .build());

        return Mapper.toDto(userRepository.save(user));
    }

    // 회원
    // 회원정보 조회
    public UserDto getUser(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return Mapper.toDto(user);
        }
        return null;
    }

    // 회원정보 수정
    @Transactional
    public UserDto updateUser(Integer id, UserDto userDto) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new EntityNotFoundException("User not found with Id: " + id);
        }

        User existingUser = optionalUser.get();
        existingUser.setName(userDto.getName());
        existingUser.setTel(userDto.getTel());
        existingUser.setIsSmsAllowed(userDto.getIsSmsAllowed());

        User updatedUser = userRepository.save(existingUser);

        // UserDto로 변환하여 반환
        return Mapper.toDto(updatedUser);
    }

    // 회원 탈퇴
    @Transactional
    public boolean deleteUser(Integer id, String accessToken, String clientId) {
        kakaoAuthService.unlinkUser(accessToken);

        return userRepository.findById(id)
                .map(user -> {
                    user.setKakaoId("");
                    user.setName("");
                    user.setTel(null);
                    user.setIsSmsAllowed(false); // 기본값 설정
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }
}