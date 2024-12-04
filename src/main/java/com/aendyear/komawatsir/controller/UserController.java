package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.auth.SessionService;
import com.aendyear.komawatsir.dto.UserDto;
import com.aendyear.komawatsir.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SessionService sessionService;

    @Value("${kakao.client.id}")
    private String clientId;
    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    // 카카오 로그인 정보 반환
    @GetMapping("/kakao/loginPage")
    @Operation(summary = "Get Kakao login page info", description = "Returns Kakao client ID and redirect URI")
    public ResponseEntity<Object> kakaoLoginPage() {
        return ResponseEntity.ok(Map.of(
                "clientId", clientId,
                "redirectUri", redirectUri
        ));
    }

    // 카카오 로그인 처리
    @GetMapping("/kakao/login-test")
    @Operation(summary = "Handle Kakao login", description = "Processes Kakao login using authorization code")
    public ResponseEntity<UserDto> getKakaoLogin(@RequestParam String code, HttpServletRequest request,HttpServletResponse response) {
        try {
            UserDto userDto = userService.getKakaoLogin(code, clientId, redirectUri, request,response);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    //카카오 로그아웃
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logs out the user using Kakao ID and access token")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = sessionService.getKakaoAccessTokenFromSession(request);
        boolean result = userService.logout(accessToken, request, response);
        if (result) {
            return "Logout successful";
        } else {
            return "Logout failed";
        }
    }

    @PostMapping //비회원 -> 회원 연결
    @Operation(summary = "Sign up with Kakao", description = "Registers a new user using Kakao information")
    public ResponseEntity<UserDto> signUpWithKakao(@RequestBody Map<String, String> payload) {
        try {
            String kakaoId = payload.get("kakaoId");
            String name = payload.get("name");
            String tel = payload.get("tel");
            if (kakaoId == null || name == null || kakaoId.trim().isEmpty()|| name.trim().isEmpty()) {
                return ResponseEntity.status(400).body(null);
            }
            UserDto userDto = userService.signUpWithKakao(kakaoId, name, tel);

            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id", description = "회원 정보 조회")
    public ResponseEntity<UserDto> getUser(@PathVariable Integer id) {
        Integer authId = userService.getAuthenticatedUser();
        if (!authId.equals(id)) {
            throw new AccessDeniedException("해당 사용자는 수정할 수 없습니다.");
        }
        return ResponseEntity.ok(userService.getUser(authId));
    }

    // 회원정보 수정
    @PutMapping("/{id}")
    @Operation(summary = "Update user details", description = "회원정보 수정")
    public ResponseEntity<UserDto> updateUser(@PathVariable Integer id,@RequestBody UserDto userDto) {
        Integer authId = userService.getAuthenticatedUser();
        if (!authId.equals(id)) {
            throw new AccessDeniedException("해당 사용자는 수정할 수 없습니다.");
        }
        UserDto updatedUser =  userService.updateUser(authId, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    // 회원 탈퇴 (pk 유지)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "회원 탈퇴")
    public ResponseEntity<UserDto> deleteUser(@PathVariable Integer id, HttpServletRequest request) {
        String accessToken = sessionService.getKakaoAccessTokenFromSession(request);
        Integer authId = userService.getAuthenticatedUser();
        if (!authId.equals(id)) {
            throw new AccessDeniedException("해당 사용자는 탈퇴할 수 없습니다.");
        }

        boolean isDeleted = userService.deleteUser(authId, accessToken, clientId);
        if (isDeleted) {
            return ResponseEntity.ok().build();  // 200 OK 응답 (탈퇴 성공)
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // 토큰 관련
    @GetMapping("/token/validate")
    @Operation(summary = "token validate", description = "토큰 검증")
    public ResponseEntity<Boolean> validateToken(HttpServletRequest request) {
//        return ResponseEntity.ok(userService.validateToken());
        System.out.println(request.getHeader("Authorization"));
        return null;
    }
}
