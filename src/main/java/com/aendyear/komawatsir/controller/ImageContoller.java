package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.dto.ImageDto;
import com.aendyear.komawatsir.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
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

    @GetMapping("/image/{imageId}")
    @Operation(summary = "load single image", description = "단일 이미지 조회")
    public ResponseEntity<ImageDto> getSingleImage(@PathVariable Integer imageId) {
        return ResponseEntity.ok(imageService.getSingleImage(imageId));
    }

    // todo : 카테고리 + 페이징...
    // 이미지 목록 조회
//    @GetMapping("/{type}")
}
