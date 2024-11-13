package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.service.FontService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(name = "/api/fonts")
public class FontController {

    @Autowired
    private FontService fontService;

    // 단일 폰트 조회 (url 가져오기)
//    @GetMapping("/{id}")

    // 폰트 목록 조회
//    @GetMapping

    // 폰트 변경 (수정)
//    @PutMapping("/{id}")
}
