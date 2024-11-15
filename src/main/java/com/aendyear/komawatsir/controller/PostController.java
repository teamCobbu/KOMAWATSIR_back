package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.dto.PostDesignDto;
import com.aendyear.komawatsir.dto.PostDto;
import com.aendyear.komawatsir.entity.Post;
import com.aendyear.komawatsir.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {

    @Autowired
    private PostService postService;

    // 연하장 생성
//    @PostMapping("/posts")

    // 단일 연하장 조회
//    @GetMapping("/posts/{id}")

    @GetMapping("/receivers/{receiverUserId}/posts/{year}")
    @Operation(summary = "show card by receiver", description = "연도별 받은 연하장 조회")
    public ResponseEntity<List<PostDesignDto>> getShowCard(@PathVariable Integer receiverUserId, @PathVariable String year) {
        return ResponseEntity.ok(postService.getShowCard(receiverUserId, year));
    }

    // 연하장 상태 수정 (작성 전, 중, 완, 삭제)
//    @PatchMapping("/posts/{id}/status")

    @GetMapping("/posts/write/gpt")
    @Operation(summary = "auto create post content", description = "챗 지피티를 사용한 자동 연하장 내용 생성")
    public ResponseEntity<String> getUseGpt(@RequestParam String prompt) {
        return ResponseEntity.ok(postService.getUseGpt(prompt));
    }
}
