package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.auth.KakaoAuthService;
import com.aendyear.komawatsir.auth.KakaoUserService;
import com.aendyear.komawatsir.dto.UserDto;
import com.aendyear.komawatsir.entity.User;
import com.aendyear.komawatsir.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
public class UserService {

    private final KakaoAuthService kakaoAuthService;
    private final KakaoUserService kakaoUserService;
    private final UserRepository userRepository;

    @Autowired
    public UserService(KakaoAuthService kakaoAuthService, KakaoUserService kakaoUserService, UserRepository userRepository) {
        this.kakaoAuthService = kakaoAuthService;
        this.kakaoUserService = kakaoUserService;
        this.userRepository = userRepository;
    }

    // ****  회원 가입 및 로그인, 로그아웃  *****
    // 카카오 로그인
    public User getKakaoLogin(String code, String clientId, String redirectUri) {
        String accessToken = parseAccessToken(kakaoAuthService.getAccessToken(code, clientId, redirectUri));

        if (accessToken == null) {
            throw new RuntimeException("Failed to retrieve access token.");
        }
        return findOrSaveUser(getUserInfoFromKakao(accessToken));
    }

    // Access Token을 JSON에서 추출
    private String parseAccessToken(String kakaoTokenJson) {
        try {
            return new ObjectMapper().readTree(kakaoTokenJson).get("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON response: " + e.getMessage());
        }
    }

    private User getUserInfoFromKakao(String accessToken) {
        return Mapper.toEntity(kakaoUserService.getKakaoUserInfo(accessToken));
    }

    // 사용자 정보를 데이터베이스에서 조회하거나, 없으면 새로 저장
    private User findOrSaveUser(User user) {
        return userRepository.findByKakaoId(user.getKakaoId()).orElseGet(() -> userRepository.save(user));
    }

    //로그아웃
    public boolean logout( String accessToken, HttpServletRequest request, HttpServletResponse response) {
        if (!kakaoAuthService.logout(accessToken)) return false;
        request.getSession().invalidate();
        deleteCookie(response, "access_token");
        deleteCookie(response, "refresh_token");
        return true;
    }

    // 쿠키 삭제 메서드
    private void deleteCookie(HttpServletResponse response, String cookieName) {
        var cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0); // 쿠키 만료 설정
        cookie.setPath("/"); // 쿠키의 유효한 경로
        response.addCookie(cookie);
    }

    // 비회원 -> 회원 연결
    @Transactional
    public UserDto signUpWithKakao(String kakaoId, String name, String tel) {

        Optional<User> existingUserOpt = userRepository.findByTel(tel);

        // 기존 사용자가 있는 경우
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get(); // 값 꺼내기

            // 기존 사용자 정보 업데이트
            existingUser.setKakaoId(kakaoId);
            existingUser.setName(name);

            User updatedUser = userRepository.save(existingUser);
            return Mapper.toDto(updatedUser); // Entity → DTO 변환
        }

        // 새로운 회원 생성
        User newUser = User.builder()
                .kakaoId(kakaoId)
                .name(name)
                .tel(tel)
                .build();
        User savedUser = userRepository.save(newUser);
        return Mapper.toDto(savedUser); // Entity → DTO 변환
    }

    // 회원
    // 회원정보 조회
    public User getUser(Integer id) {
        return userRepository.findById(id).orElse(null);
    }

    // 회원정보 수정
    @Transactional
    public User updateUser(Integer id, UserDto userDto) {
        User existingUser = userRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("User not found with Id: " + id)
        );

        existingUser.setName(userDto.getName());
        existingUser.setTel(userDto.getTel());
        existingUser.setIsSmsAllowed(userDto.getIsSmsAllowed());
        return userRepository.save(existingUser);
    }

    // 회원 탈퇴
    @Transactional
    public boolean deleteUser(Integer id) {
        Optional<User> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // kakaoId를 null로 설정 (PK는 그대로 남아있음)
            user.setKakaoId("");
            user.setName(""); // 다른 정보도 null로 설정 가능
            user.setTel(null);
            user.setIsSmsAllowed(false); // 기본값으로 설정
            userRepository.save(user); // 변경된 정보를 DB에 저장

            return true;
        }
        return false; // 사용자 존재하지 않으면 false 반환
    }

}
