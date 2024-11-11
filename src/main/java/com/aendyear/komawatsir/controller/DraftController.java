package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.service.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/users/{userId}/drafts")
public class DraftController {

    @Autowired
    private DraftService draftService;

    // 초안 추가
//    @PostMapping

    // 단일 초안 조회
//    @GetMapping("/{draftId}")

    // 초안 목록 조회
//    @GetMapping

    // 초안 삭제
//    @DeleteMapping("/{draftId}")
}
