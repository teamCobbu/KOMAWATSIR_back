package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.entity.User;
import com.aendyear.komawatsir.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
//@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Value("${kakao.client.id}")
    private String clientId;

    @Value("${kakao.redirect.uri}")
    private String redirectUri;

    // 회원가입
//    @PostMapping

    // 카카오 로그인

    @GetMapping("/api/users/kakao/loginPage")
    public String kakaoLoginPage(Model model) {
        model.addAttribute("clientId", clientId);
        model.addAttribute("redirectUri", redirectUri);
        return "login";
    }

    @PostMapping("/kakao/login")
    public ResponseEntity<Object> kakaoLogin(@RequestParam String code) {
        try {
            User user = userService.processKakaoLogin(code, clientId, redirectUri);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("카카오 로그인 처리 중 오류가 발생했습니다.");
        }
    }

//    @PostMapping("/kakao/login")
//    public ResponseEntity<Object> kakaoLogin(@RequestParam String code,
//                                        @RequestParam String clientId,
//                                        @RequestParam String redirectUri) {
//        try {
//            // 카카오 로그인 처리
//            User user = userService.processKakaoLogin(code, clientId, redirectUri);
//            // 로그인 성공 시 사용자 정보 반환
//            return ResponseEntity.ok(user);
//        } catch (Exception e) {
//            // 오류 처리: 예외 발생 시 500 에러와 메시지 반환
//            return ResponseEntity.status(500).body("카카오 로그인 처리 중 오류가 발생했습니다.");
//        }
//    }

    // 회원정보 조회
//    @GetMapping("/{id}")

    // 회원정보 수정
//    @PutMapping("/{id}")

    // 탈퇴
//    @DeleteMapping("/{id}")

}
