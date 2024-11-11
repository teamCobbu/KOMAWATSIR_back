package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.service.DesignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(name = "/api/users/{userId}/designs")
public class DesignController {

    @Autowired
    private DesignService designService;

    // 디자인 추가
//    @PostMapping

    // 디자인 정보 수정
//    @PutMapping("/{designId}")

    // 디자인 삭제
//    @DeleteMapping("/{designId")
}
