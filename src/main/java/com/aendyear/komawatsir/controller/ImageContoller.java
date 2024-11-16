package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.dto.ImageDto;
import com.aendyear.komawatsir.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageContoller {

    @Autowired
    private ImageService imageService;

    // todo : 이미지 서버 업로드 일단 모르는 척
//    @PostMapping("/{type}")
//    @Operation(summary = "add image", description = "이미지 업로드")

    @GetMapping("/image/{imageId}")
    @Operation(summary = "load single image", description = "단일 이미지 조회")
    public ResponseEntity<ImageDto> getSingleImage(@PathVariable Integer imageId) {
        return ResponseEntity.ok(imageService.getSingleImage(imageId));
    }

    // todo : 카테고리 + 페이징...
    // 이미지 목록 조회
//    @GetMapping("/{type}")

    @GetMapping("/category/list")
    @Operation(summary = "load category list", description = "이미지 카테고리 목록 조회")
    public ResponseEntity<List<String>> getCategoryList() {
        return ResponseEntity.ok(imageService.getCategoryList());
    }

}
