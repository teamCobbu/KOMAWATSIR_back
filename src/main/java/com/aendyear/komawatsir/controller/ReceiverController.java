package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.dto.ReceiverDto;
import com.aendyear.komawatsir.dto.ReceiverQuestionDto;
import com.aendyear.komawatsir.dto.UserDto;
import com.aendyear.komawatsir.entity.Receiver;
import com.aendyear.komawatsir.entity.User;
import com.aendyear.komawatsir.service.ReceiverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping(name = "/api/users/{userId}/receivers")
public class ReceiverController {

    @Autowired
    private ReceiverService receiverService;

    @PostMapping
    @Operation(summary = "add receiver", description = "수신인 추가하기")
    public ResponseEntity<Receiver> postAddReceiver(@PathVariable Integer userId, @RequestBody ReceiverDto dto) {
        return ResponseEntity.ok(receiverService.postAddReceiver(userId, dto));
    }


    @GetMapping("/{receiverId}")
    @Operation(summary = "receiver question list", description = "수신인 설문 조회하기")
    public ResponseEntity<List<ReceiverQuestionDto>> getReceiverQuestion(@PathVariable(name = "userId") Integer senderId, @PathVariable(name = "receiverId") Integer receiverUserId) {
        return ResponseEntity.ok(receiverService.getReceiverQuestion(senderId, receiverUserId));
    }

    @GetMapping
    @Operation(summary = "receiver list", description = "수신인 목록 조회하기")
    public ResponseEntity<List<ReceiverDto>> getReceiverList(@PathVariable Integer userId) {
        return ResponseEntity.ok(receiverService.getReceiverList(userId));
    }
}
