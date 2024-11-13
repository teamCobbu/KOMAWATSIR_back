package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(name = "/api")
public class PostController {

    @Autowired
    private PostService postService;

    // 연하장 생성
//    @PostMapping("/posts")

    // 단일 연하장 조회
//    @GetMapping("/posts/{id}")

    // 수신인별 받은 연하장 조회
//    @GetMapping("/receivers/{reveicerId}/posts")

    // 연하장 상태 수정 (작성 전, 중, 완, 삭제)
//    @PatchMapping("/posts/{id}/status")
}
