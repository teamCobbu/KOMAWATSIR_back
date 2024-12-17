package com.aendyear.komawatsir.service;

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
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public UserService(KakaoAuthService kakaoAuthService, KakaoUserService kakaoUserService, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.kakaoAuthService = kakaoAuthService;
        this.kakaoUserService = kakaoUserService;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // ****  회원 가입 및 로그인, 로그아웃  *****
    // 카카오 로그인
    public UserDto getKakaoLogin(String code, String clientId, String redirectUri, HttpServletRequest request, HttpServletResponse response) {
        String accessToken = parseAccessToken(kakaoAuthService.getAccessToken(code, clientId, redirectUri));
        System.out.println("US.accessToken = " + accessToken);
        if (accessToken == null) {
            throw new RuntimeException("Failed to retrieve access token.");
        }

        User userInfo = getUserInfoFromKakao(accessToken);
        System.out.println("Kakao User Info: " + userInfo);
        if (userInfo == null) {
            throw new RuntimeException("User info from Kakao is null");
        }

        User user = findOrSaveUser(getUserInfoFromKakao(accessToken));

        System.out.println("US.user id22 = " + user.getId() +" -- "+ user.getKakaoId());
        UserDto userDto = new UserDto(user);
        System.out.println("US.userDto = " + userDto);

        String jwtToken = jwtTokenProvider.createToken(user.getKakaoId());
        System.out.println("US.jwtToken = " + jwtToken);
        addJwtToCookie(response, jwtToken);

        request.getSession().setAttribute("kakao_access_token", accessToken);

        return userDto;
    }

    //인증된 사용자
    public Integer getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("authentication = " + authentication);
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("인증되지 않은 사용자입니다.");
        }

        String kakaoId = authentication.getName();
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return user.getId();
    }

    private void addJwtToCookie(HttpServletResponse response, String jwtToken) {
        Cookie cookie = new Cookie("JWT", jwtToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);    // HTTPS에서만 (SSL인증)
        cookie.setPath("/");
        cookie.setMaxAge(3600);
        cookie.setDomain("xn--299au8vhphgpd.com");  // 쿠키가 적용될 도메인
        response.addCookie(cookie);
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
        System.out.println("Us.accessToken = " + accessToken);
        return Mapper.toEntity(kakaoUserService.getKakaoUserInfo(accessToken));
    }

    // 사용자 정보를 데이터베이스에서 조회하거나, 없으면 새로 저장
    private User findOrSaveUser(User user) {
        System.out.println("getId11 : " + user.getId());
        System.out.println("getTel11 : " + user.getTel());
        System.out.println("getName : " + user.getName());
        System.out.println("getKakaoId : " + user.getKakaoId());
        System.out.println("getIsSmsAllowed : " + user.getIsSmsAllowed());
        Optional<User> checkUser = userRepository.findByKakaoId(user.getKakaoId());
        if(checkUser.isPresent()) {
           user.setId(checkUser.get().getId());
           user.setTel(checkUser.get().getTel());
           user.setIsSmsAllowed(checkUser.get().getIsSmsAllowed());

            System.out.println("getId12 : " + user.getId());
            System.out.println("getTel12 : " + user.getTel());
        } else {
            user.setIsSmsAllowed(false);
        }
        return userRepository.save(user);
    }

    //로그아웃
    public boolean logout(String accessToken, HttpServletRequest request, HttpServletResponse response) {
        if (!kakaoAuthService.logout(accessToken)) return false;
        request.getSession().invalidate();
        deleteCookie(response, "JWT");
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
            return Mapper.toDto(updatedUser);
        }

        // 새로운 회원 생성
        User newUser = User.builder()
                .kakaoId(kakaoId)
                .name(name)
                .tel(tel)
                .isSmsAllowed(false)
                .build();
        User savedUser = userRepository.save(newUser);
        return Mapper.toDto(savedUser);
    }

    // 회원
    // 회원정보 조회
    public UserDto getUser(Integer id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            return UserDto.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .tel(user.getTel())
                    .kakaoId(user.getKakaoId())
                    .isSmsAllowed(user.getIsSmsAllowed())
                    .build();
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
        return new UserDto(updatedUser);
    }

    // 회원 탈퇴
    @Transactional
    public boolean deleteUser(Integer id, String accessToken, String clientId) {
        Optional<User> userOpt = userRepository.findById(id);
        // 카카오 탈퇴 처리
        kakaoAuthService.unlinkUser(accessToken);
        // 유저DB 탈퇴 처리
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            // kakaoId를 null로 설정 (PK는 그대로)
            user.setKakaoId("");
            user.setName("");
            user.setTel(null);
            user.setIsSmsAllowed(false); // 기본값
            userRepository.save(user); // 변경된 정보를 DB에 저장
            return true;
        }
        return false; // 사용자 존재하지 않으면 false 반환
    }

    public boolean validateToken(Integer userId, HttpServletRequest request) {
        boolean b = false;
        String token = jwtTokenProvider.resolveToken(request);
        if (jwtTokenProvider.validateToken(token)) {
            String kakaoId = jwtTokenProvider.getUserId(token);
            Optional<User> userInfo = userRepository.findByKakaoId(kakaoId);
            if (userInfo.isPresent()) {
                b = (userInfo.get().getId().equals(userId));
            }
        }
        return b;
    }
}
