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
        // 카카오 API 호출하여 Access Token JSON 응답 받기
        String kakaoTokenJson = kakaoAuthService.getAccessToken(code, clientId, redirectUri);

        // JSON 응답에서 access_token 추출
        String accessToken = parseAccessToken(kakaoTokenJson);
        if (accessToken == null) {
            System.err.println("Failed to retrieve access token.");
            return null;
        }

        // Access Token으로 사용자 정보 가져오기
        User user = getUserInfoFromKakao(accessToken);
        // 기존 사용자 정보 확인 및 저장 로직
        return findOrSaveUser(user);
    }

    // Access Token을 JSON에서 추출
    private String parseAccessToken(String kakaoTokenJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(kakaoTokenJson);
            return jsonNode.get("access_token").asText();
        } catch (Exception e) {
            System.err.println("Error parsing JSON response: " + e.getMessage());
            return null;
        }
    }

    private User getUserInfoFromKakao(String accessToken) {
        try {
            UserDto userDto = kakaoUserService.getKakaoUserInfo(accessToken);
            User user = Mapper.toEntity(userDto);
            return user;
        } catch (Exception e) {
            System.err.println("Error in getUserInfoFromKakao: " + e.getMessage());
            throw new RuntimeException("Failed to retrieve user info from Kakao", e);
        }
    }

    // 사용자 정보를 데이터베이스에서 조회하거나, 없으면 새로 저장
    private User findOrSaveUser(User user) {
        try {
            User existingUser = userRepository.findByKakaoId(user.getKakaoId()).orElse(null);

            if (existingUser != null) {
                // 이미 존재하는 사용자라면 기존 사용자 반환
                return existingUser;
            } else {
                // 새로운 사용자라면 저장 후 반환
                return userRepository.save(user);
            }
        } catch (Exception e) {
            System.err.println("Error in findOrSaveUser: " + e.getMessage());
            e.printStackTrace();
            throw e; // 예외를 재발생시켜 상위 로직에서 처리
        }
    }

    //로그아웃
    public boolean logout(String kakaoId, String accessToken, HttpServletRequest request, HttpServletResponse response) {
        boolean kakaoLogoutSuccess = kakaoAuthService.logout(accessToken);
        if (!kakaoLogoutSuccess) {
            System.err.println("Kakao logout failed.");
            return false;
        }
        // 세션 무효화
        request.getSession().invalidate();

        // 쿠키 삭제 (예시: 로그인 시 저장한 쿠키 삭제)
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
    public UserDto signUpWithKakao(String kakaoId, String name, String tel) {
        Optional<User> existingUserOpt = userRepository.findByTel(tel);

        // 기존 사용자가 있는 경우
        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get(); // 값 꺼내기
            System.out.println("signUpWithKakao : " + existingUser.getTel()); // 기존 사용자 전화번호 출력

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
    public User getUser(String KakaoId) {
        return userRepository.findByKakaoId(KakaoId).orElse(null);
    }

    // 회원정보 수정
    public User updateUser(String kakaoId, UserDto userDto) {
        User existingUser = userRepository.findByKakaoId(kakaoId).orElseThrow(() ->
                new IllegalArgumentException("User not found with kakaoId: " + kakaoId)
        );

        existingUser.setName(userDto.getName());
        existingUser.setTel(userDto.getTel());
        existingUser.setIsSmsAllowed(userDto.getIsSmsAllowed());
        return userRepository.save(existingUser);
    }

    // 회원 탈퇴
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
