package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.auth.AuthService;
import com.aendyear.komawatsir.auth.SessionService;
import com.aendyear.komawatsir.dto.UserDto;
import com.aendyear.komawatsir.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private AuthService authService;

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
//            log.error("Kakao login error", e);
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
            String kakaoId = payload.get("kakaoId");
            String name = payload.get("name");
            String tel = payload.get("tel");
            if (kakaoId == null || name == null || kakaoId.trim().isEmpty()|| name.trim().isEmpty()) {
                return ResponseEntity.status(400).body(null);
            }
            UserDto userDto = userService.signUpWithKakao(kakaoId, name, tel);
            return ResponseEntity.ok(userDto);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id", description = "회원 정보 조회")
    public ResponseEntity<UserDto> getUser(@PathVariable Integer id) {
        System.out.println("로그확인");
        Integer authId = userService.getAuthenticatedUser();
        if (authId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null); // 인증되지 않은 경우 처리
        }
        if (!authId.equals(id)) {
            throw new AccessDeniedException("사용자를 조회할 수 없습니다.");
        }
        return ResponseEntity.ok(userService.getUser(authId));
    }

    // 회원정보 수정
    @PutMapping("/{id}")
    @Operation(summary = "Update user details", description = "회원정보 수정")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody @Valid UserDto userDto, BindingResult bindingResult) {
        Integer authId = userService.getAuthenticatedUser();
        if (!authId.equals(id)) {
            throw new AccessDeniedException("해당 사용자는 수정할 수 없습니다.");
        }
        if (bindingResult.hasErrors()) {
            List<String> errorMessages = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(errorMessages);
        }
            UserDto updatedUser = userService.updateUser(id, userDto);
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
    @GetMapping("/token/validate/{userId}")
    @Operation(summary = "token validate", description = "토큰 검증")
    public ResponseEntity<Boolean> validateToken(@PathVariable Integer userId, HttpServletRequest request) {
        return ResponseEntity.ok(authService.validateToken(userId, request));
    }
}