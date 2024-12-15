package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.dto.PostDesignDto;
import com.aendyear.komawatsir.dto.PostDto;
import com.aendyear.komawatsir.entity.Image;
import com.aendyear.komawatsir.entity.Post;
import com.aendyear.komawatsir.service.PostService;
import com.aendyear.komawatsir.type.ImageCategory;
import com.aendyear.komawatsir.type.SourceType;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        return ResponseEntity.ok(postService.getUseGpt(prompt));
    }

    @GetMapping("/posts/design/{userId}")
    @Operation(summary = "load post design", description = "포스트 디자인 가져오기")
    public ResponseEntity<PostDesignDto> getPostDesign(@PathVariable Integer userId) {
        return ResponseEntity.ok(postService.getPostDesign(userId));
    }

    @GetMapping("/receivers/{receiverUserId}/posts/all")
    @Operation(summary = "show card by receiver", description = "받은 전체 연하장 조회")
    public ResponseEntity<List<PostDesignDto>> getCardsByUser(@PathVariable Integer receiverUserId) {
        return ResponseEntity.ok(postService.getCardsByUser(receiverUserId));
    }

    @PutMapping("/posts/{postId}")
    @Operation(summary = "show card by receiver", description = "연하장 이미지 처리")
    public ResponseEntity<String> savePostImage(@PathVariable Integer postID, @RequestParam("image") MultipartFile image) {
        return ResponseEntity.ok(postService.savePostImage(postID, image));
    }

    // todo: 테스트용 -> 추후 삭제
    @GetMapping("/posts/all")
    @Operation(summary = "load post design", description = "포스트 디자인 가져오기")
    public ResponseEntity<List<PostDesignDto>> getAllCards() {
        return ResponseEntity.ok(postService.getAllCards());
    }

}
