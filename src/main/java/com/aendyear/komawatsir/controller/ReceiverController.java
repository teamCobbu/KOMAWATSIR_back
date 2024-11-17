package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.dto.ReceiverDto;
import com.aendyear.komawatsir.dto.ReceiverQuestionDto;
import com.aendyear.komawatsir.service.ReceiverService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/receivers")
public class ReceiverController {

    @Autowired
    private ReceiverService receiverService;

    @PostMapping
    @Operation(summary = "add receiver", description = "수신인 추가하기")
    public ResponseEntity<?> postAddReceiver(@PathVariable(name = "userId") Integer senderId, @RequestBody ReceiverDto dto) {

        // true 일 경우 이미 신청된 전화번호
        if (receiverService.duplicationCheck(senderId, dto.getTel())) {
            return ResponseEntity.badRequest().body("이미 신청된 전화번호입니다.");
        }

        return ResponseEntity.ok(receiverService.postAddReceiver(senderId, dto));
    }

    @GetMapping("/{receiverId}")
    @Operation(summary = "receiver question list", description = "수신인 설문 조회하기")
    public ResponseEntity<List<ReceiverQuestionDto>> getReceiverQuestion(@PathVariable(name = "userId") Integer senderId, @PathVariable Integer receiverId) {
        return ResponseEntity.ok(receiverService.getReceiverQuestion(senderId, receiverId));
    }

    @GetMapping
    @Operation(summary = "receiver list", description = "수신인 목록 조회하기")
    public ResponseEntity<List<ReceiverDto>> getReceiverList(@PathVariable Integer userId) {
        return ResponseEntity.ok(receiverService.getReceiverList(userId));
    }
}
