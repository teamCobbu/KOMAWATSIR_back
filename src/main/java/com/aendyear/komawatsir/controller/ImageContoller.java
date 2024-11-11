package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/images")
public class ImageContoller {

    @Autowired
    private ImageService imageService;
    // 이미지 추가
//    @PostMapping("/{type}")

    // 이미지 조회 (단일 이미지)
//    @GetMapping("/{type}/{id}")

    // 이미지 목록 조회
//    @GetMapping("/{type}")

    // 이미지 카테고리 목록 조회
//    @GetMapping("/{category}")

    // 이미지 정보 수정
//    @PutMapping("/{type}/{id}")
}
