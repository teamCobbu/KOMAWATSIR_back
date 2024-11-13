package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.dto.InquiryItemDto;
import com.aendyear.komawatsir.entity.Inquiry;
import com.aendyear.komawatsir.entity.InquiryItem;
import com.aendyear.komawatsir.service.InquiryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiry/{userId}")
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    @GetMapping
    @Operation(summary = "inquiry question list", description = "질문 목록 불러오기")
    public ResponseEntity<List<InquiryItemDto>> getQuestionList(@PathVariable Integer userId) {
        System.out.println(userId);
        return ResponseEntity.ok(inquiryService.getQuestionList(userId));
    }

    @PostMapping("/{nickname}")
    @Operation(summary = "add inquiry", description = "닉네임 설정하기")
    public ResponseEntity<Inquiry> postQuestion(@PathVariable Integer userId, @PathVariable String nickname) {
        return ResponseEntity.ok(inquiryService.postQuestion(userId, nickname));
    }

    @PostMapping
    @Operation(summary = "add inquiry question", description = "질문 추가하기")
    public ResponseEntity<InquiryItem> postInsertQuestion(@PathVariable Integer userId, @RequestBody InquiryItemDto dto) {
        return ResponseEntity.ok(inquiryService.postInsertQuestion(userId, dto));
    }

    @PutMapping
    @Operation(summary = "edit inquiry question", description = "질문 수정하기")
    public ResponseEntity<InquiryItem> putUpdateQuestion(@RequestBody InquiryItemDto dto) {
        return ResponseEntity.ok(inquiryService.putUpdateQuestion(dto));
    }

    @DeleteMapping
    @Operation(summary = "remove inquiry question", description = "질문 삭제하기")
    public ResponseEntity<Integer> deleteRemoveQuestion(@RequestBody InquiryItemDto dto) {
        return ResponseEntity.ok(inquiryService.deleteRemoveQuestion(dto));
    }
}
