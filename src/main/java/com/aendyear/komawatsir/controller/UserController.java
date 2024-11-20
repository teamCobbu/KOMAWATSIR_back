package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.dto.UserDto;
import com.aendyear.komawatsir.entity.User;
import com.aendyear.komawatsir.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

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
    public ResponseEntity<Object> getKakaoLogin(@RequestParam String code) {
        try {
            User user = userService.getKakaoLogin(code, clientId, redirectUri);
            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "kakaoId", user.getKakaoId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "카카오 로그인 처리 중 오류가 발생했습니다.",
                    "details", e.getMessage()
            ));
        }
    }

    //카카오 로그아웃
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logs out the user using Kakao ID and access token")
    public String logout(@RequestParam String kakaoId, @RequestParam String accessToken, HttpServletRequest request, HttpServletResponse response) {
        boolean result = userService.logout(accessToken, request, response);
        if (result) {
            return "Logout successful";
        } else {
            return "Logout failed";
        }
    }

    @PostMapping //비회원 -> 회원 연결
    @Operation(summary = "Sign up with Kakao", description = "Registers a new user using Kakao information")
    public ResponseEntity<Object> signUpWithKakao(@RequestBody Map<String, String> payload) {
        try {
            String kakaoId = payload.get("kakaoId");
            String name = payload.get("name");
            String tel = payload.get("tel");

            // Null 체크 추가
            if (kakaoId == null || name == null || tel == null) {
                return ResponseEntity.status(400).body(Map.of(
                        "error", "Missing required fields",
                        "details", "kakaoId, name, and tel must be provided"
                ));
            }

            UserDto userDto = userService.signUpWithKakao(kakaoId, name, tel);
            return ResponseEntity.ok(userDto);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to sign up",
                    "details", e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id", description = "회원 정보 조회")
    public ResponseEntity<Object> getUser(@PathVariable Integer id) {
        try {
            User user = userService.getUser(id);
            if (user == null) {
                return ResponseEntity.status(404).body(Map.of(
                        "error", "User not found",
                        "id", id
                ));
            }

            return ResponseEntity.ok(Map.of(
                    "id", user.getId(),
                    "name", user.getName(),
                    "kakaoId", user.getKakaoId() == null ? "N/A" : user.getKakaoId(),
                    "tel", user.getTel(),
                    "isSmsAllowed", user.getIsSmsAllowed()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to retrieve user",
                    "details", e.getMessage()
            ));
        }
    }

    // 회원정보 수정
    @PutMapping("/{id}")
    @Operation(summary = "Update user details", description = "회원정보 수정")
    public ResponseEntity<Object> updateUser(@PathVariable Integer id, @RequestBody UserDto userDto) {
        try {
            User updatedUser = userService.updateUser(id, userDto);  // id를 이용해 사용자 업데이트
            return ResponseEntity.ok(Map.of(
                    "id", updatedUser.getId(),
                    "name", updatedUser.getName(),
                    "tel", updatedUser.getTel(),
                    "isSmsAllowed", updatedUser.getIsSmsAllowed()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(Map.of(
                    "error", "Invalid data provided",
                    "details", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to update user",
                    "details", e.getMessage()
            ));
        }
    }

    // 회원 탈퇴 (pk 유지)
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "회원 탈퇴")
    public ResponseEntity<Object> deleteUser(@PathVariable Integer id) {  // id로 삭제
        try {
            boolean isDeleted = userService.deleteUser(id);  // id로 사용자 삭제
            if (isDeleted) {
                return ResponseEntity.ok(Map.of(
                        "message", "User deleted successfully",
                        "id", id
                ));
            } else {
                return ResponseEntity.status(404).body(Map.of(
                        "error", "User not found",
                        "id", id
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "error", "Failed to delete user",
                    "details", e.getMessage()
            ));
        }
    }
}
