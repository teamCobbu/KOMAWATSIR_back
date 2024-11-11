package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(name = "/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    // 회원가입
//    @PostMapping

    // 회원정보 조회
//    @GetMapping("/{id}")

    // 회원정보 수정
//    @PutMapping("/{id}")

    // 탈퇴
//    @DeleteMapping("/{id}")

}
