package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.dto.PostDto;
import com.aendyear.komawatsir.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(name = "/api")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/posts")
    @Operation(summary = "Create a new post", description = "연하장 생성")
    public ResponseEntity<Object> createPost(@RequestBody PostDto postDto) {
        return ResponseEntity.ok(postService.createPost(postDto));
    }

    @GetMapping("/posts/{id}")
    @Operation(summary = "Get a single post", description ="단일 연하장 조회")
    public ResponseEntity<Object> getPostById(@PathVariable Integer id) {
        return ResponseEntity.ok(postService.getPostById(id));
    }

    @GetMapping("/receivers/{receiverId}/posts")
    @Operation(summary = "Get posts by receiver", description ="수신인별 받은 연하장 조회")
            public ResponseEntity<Object> getPostsByReceiverId(@PathVariable Integer receiverId) {
        return ResponseEntity.ok(postService.getPostsByReceiverId(receiverId));
    }

    @PatchMapping("/posts/{id}/status")
    @Operation(summary = "Update post status", description ="연하장 작성 상태 수정")
    public ResponseEntity<Object> updatePostStatus(@PathVariable Integer id, @RequestBody String status) {
        return ResponseEntity.ok(postService.updatePostStatus(id, status));
    }

    @PatchMapping("/{id}/delete")
    @Operation(summary = "Delete post", description = "연하장 삭제")
    public ResponseEntity<?> deletePost(@PathVariable Integer id) {
        // 상태를 'DELETED'로 변경
        return ResponseEntity.ok(postService.updatePostStatus(id, "DELETED"));
    }

}
