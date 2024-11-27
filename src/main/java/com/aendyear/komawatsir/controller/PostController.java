package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.dto.PostDesignDto;
import com.aendyear.komawatsir.dto.PostDto;
import com.aendyear.komawatsir.entity.Post;
import com.aendyear.komawatsir.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping("/posts/{status}")
    @Operation(summary = "add post", description = "연하장 임시 저장 혹은 저장")
    public ResponseEntity<Post> postAddPost(@PathVariable String status, @RequestBody PostDto dto) {
        if (status.equals("progressing") || status.equals("completed")) {
            return ResponseEntity.ok(postService.postAddPost(status, dto));
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/posts/{postId}")
    @Operation(summary = "show single card", description = "단일 연하장 조회")
    public ResponseEntity<PostDto> getSinglePost(@PathVariable Integer postId) {
        return ResponseEntity.ok(postService.getSinglePost(postId));
    }

    @GetMapping("/posts/check/{userId}/{receiverId}")
    @Operation(summary = "show single card", description = "연하장 작성 여부 조회")
    public ResponseEntity<Integer> getPostCheck(@PathVariable Integer userId, @PathVariable Integer receiverId) {
        return ResponseEntity.ok(postService.getPostCheck(userId, receiverId));
    }

    @GetMapping("/receivers/{receiverUserId}/posts/{year}")
    @Operation(summary = "show card by receiver", description = "연도별 받은 연하장 조회")
    public ResponseEntity<List<PostDesignDto>> getShowCard(@PathVariable Integer receiverUserId, @PathVariable String year) {
        return ResponseEntity.ok(postService.getShowCard(receiverUserId, year));
    }

    @PatchMapping("/posts/{postId}/delete")
    @Operation(summary = "delete post", description = "연하장 삭제 (상태 변경)")
    public ResponseEntity<Integer> patchDeletePost(@PathVariable Integer postId) {
        return ResponseEntity.ok(postService.patchDeletePost(postId));
    }

    @GetMapping("/posts/write/gpt")
    @Operation(summary = "auto create post content", description = "챗 지피티를 사용한 자동 연하장 내용 생성")
    public ResponseEntity<String> getUseGpt(@RequestParam String prompt) {
        System.out.println("hi");
        return ResponseEntity.ok(postService.getUseGpt(prompt));
    }
}
