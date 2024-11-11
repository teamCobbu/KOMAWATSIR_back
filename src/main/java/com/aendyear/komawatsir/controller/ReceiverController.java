package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.service.ReceiverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(name = "/api/users/{userId}/receivers")
public class ReceiverController {

    @Autowired
    private ReceiverService receiverService;

    // 수신인 추가
//    @PostMapping

    // 수신인 조회
//    @GetMapping("/{receiverId}")

    // 수신인 목록 조회
//    @GetMapping
}
