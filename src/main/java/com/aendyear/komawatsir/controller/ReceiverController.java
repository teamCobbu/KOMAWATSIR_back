package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.dto.ReceiverAdderDto;
import com.aendyear.komawatsir.dto.ReceiverDto;
import com.aendyear.komawatsir.dto.ReceiverQuestionDto;
import com.aendyear.komawatsir.entity.Receiver;
import com.aendyear.komawatsir.service.ReceiverService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users/{userId}/receivers")
public class ReceiverController {

    @Autowired
    private ReceiverService receiverService;

    @PostMapping
    @Operation(summary = "add receiver", description = "수신인 추가하기")
    public ResponseEntity<Object> postAddReceiver(@PathVariable(name = "userId") Integer senderId, @RequestBody ReceiverAdderDto dto) {

        // true 일 경우 이미 신청된 전화번호
        if (receiverService.duplicationCheck(senderId, dto.getReceiver().getTel())) {
            return ResponseEntity.badRequest().body("이미 신청된 전화번호입니다.");
        }

        return ResponseEntity.ok(receiverService.postAddReceiver(senderId, dto));
    }


    @GetMapping("/check/tel")
    @Operation(summary = "check receiver tel duplication", description = "수신인 번호 중복 확인하기")
    public ResponseEntity<Boolean> postCheckReceiverGuest(@PathVariable(name = "userId") Integer senderId, @RequestParam String tel) {
        // true 일 경우 이미 신청된 전화번호
        System.out.println("번호 중복 확인: " + tel);
        return ResponseEntity.ok(receiverService.duplicationCheck(senderId, tel));
    }

    @GetMapping("/check/id")
    @Operation(summary = "check receiver id duplication", description = "수신인 아이디 중복 확인하기")
    public ResponseEntity<Boolean> postCheckReceiverMember(@PathVariable(name = "userId") Integer senderId, @RequestParam Integer id) {
        // true 일 경우 이미 신청된 전화번호
        return ResponseEntity.ok(receiverService.duplicationCheckId(senderId, id));
    }

    @PutMapping("/{receiverId}")
    @Operation(summary = "edit memo", description = "메모 수정하기")
    public ResponseEntity<Receiver> putEditMemo(@PathVariable Integer receiverId, @RequestBody Map<String,String> memo) {
        return ResponseEntity.ok(receiverService.putEditMemo(receiverId, memo.get("memo")));

    }

    @GetMapping("/{receiverId}")
    @Operation(summary = "receiver question list", description = "수신인 설문 조회하기")
    public ResponseEntity<List<ReceiverQuestionDto>> getReceiverQuestion(@PathVariable(name = "userId") Integer senderId, @PathVariable Integer receiverId) {
        return ResponseEntity.ok(receiverService.getReceiverQuestion(senderId, receiverId));
    }

    @GetMapping
    @Operation(summary = "receiver list", description = "수신인 목록 조회하기")
    public ResponseEntity<Page<ReceiverDto>> getReceiverList(@PathVariable Integer userId, Pageable pageable, @RequestParam boolean pending, @RequestParam boolean progressing, @RequestParam boolean completed) {
        return ResponseEntity.ok(receiverService.getReceiverList(userId, pageable, pending, progressing, completed));
    }
}
