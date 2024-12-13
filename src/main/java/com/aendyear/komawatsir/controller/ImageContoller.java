package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.dto.ImageDto;
import com.aendyear.komawatsir.service.ImageService;
import com.aendyear.komawatsir.type.ImageCategory;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    // 이미지 목록 조회
    @GetMapping("/{type}/{userId}/{isFront}")
    @Operation(summary = "load all image by category", description = "카테고리별 이미지 조회")
    public ResponseEntity<List<ImageDto>> getAllImage(@PathVariable String type, @PathVariable Integer userId, @PathVariable boolean isFront) {
        ImageCategory category = type.equals("solid") ? ImageCategory.SOLID : type.equals("gradient") ? ImageCategory.GRADATION : type.equals("custom") ? ImageCategory.CUSTOM : ImageCategory.SEASON;
        return ResponseEntity.ok(imageService.getAllImage(category, userId, isFront));
    }


    @GetMapping("/analyze")
    @Operation(summary = "Image bright", description = "이미지 평균 밝기")
    public ResponseEntity<String> analyzeImage(@RequestParam String imageKey) {
        try {
            String brightnessState = imageService.analyzeImage(imageKey);
            return ResponseEntity.ok(brightnessState);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("오류 발생: " + e.getMessage());
        }
    }

}
