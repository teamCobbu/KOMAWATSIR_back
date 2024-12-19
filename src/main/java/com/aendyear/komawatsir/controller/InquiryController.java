package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.dto.InquiryItemDto;
import com.aendyear.komawatsir.dto.UserDto;
import com.aendyear.komawatsir.entity.Inquiry;
import com.aendyear.komawatsir.entity.InquiryItem;
import com.aendyear.komawatsir.service.DesignService;
import com.aendyear.komawatsir.service.InquiryService;
import com.aendyear.komawatsir.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inquiry")
public class InquiryController {

    @Autowired
    private InquiryService inquiryService;

    @Autowired
    private DesignService designService;
    @Autowired
    private UserService userService;

    @GetMapping("/{userId}")
    @Operation(summary = "inquiry question list", description = "질문 목록 불러오기")
    public ResponseEntity<List<InquiryItemDto>> getQuestionList(@PathVariable Integer userId) {
        return ResponseEntity.ok(inquiryService.getQuestionList(userId));
    }

    @GetMapping("/{userId}/nickname/check")
    @Operation(summary = "check nickname", description = "닉네임 설정 / 설문 생성 여부 확인하기")
    public ResponseEntity<Boolean> getCheckNickname(@PathVariable Integer userId) {
        return ResponseEntity.ok(inquiryService.getCheckNickname(userId));
    }

    @PostMapping("/{userId}/{nickname}")
    @Operation(summary = "add inquiry", description = "닉네임 설정하기")
    public ResponseEntity<Inquiry> postQuestion(@PathVariable Integer userId, @PathVariable String nickname) {
        Inquiry inquiry = inquiryService.postQuestion(userId, nickname);
        designService.addDesign(userId);
        return ResponseEntity.ok(inquiry);
    }

    @PostMapping("/{userId}")
    @Operation(summary = "add inquiry question", description = "질문 추가하기")
    public ResponseEntity<InquiryItem> postInsertQuestion(@PathVariable Integer userId, @RequestBody InquiryItemDto dto) {
        return ResponseEntity.ok(inquiryService.postInsertQuestion(userId, dto));
    }

    @PutMapping("/{userId}")
    @Operation(summary = "edit inquiry question", description = "질문 수정하기")
    public ResponseEntity<InquiryItem> putUpdateQuestion(@RequestBody InquiryItemDto dto) {
        return ResponseEntity.ok(inquiryService.putUpdateQuestion(dto));
    }

    @DeleteMapping("/{userId}/{deleteId}")
    @Operation(summary = "remove inquiry question", description = "질문 삭제하기")
    public ResponseEntity<Integer> deleteRemoveQuestion(@PathVariable Integer deleteId) {
        return ResponseEntity.ok(inquiryService.deleteRemoveQuestion(deleteId));
    }

    @GetMapping("/{userId}/get/url")
    @Operation(summary = "get hmac url", description = "링크 암호화")
    public ResponseEntity<String> getUrl(@PathVariable Integer userId) {
        return ResponseEntity.ok(inquiryService.getUrl(userId));
    }

    @GetMapping("/validate/url")
    @Operation(summary = "validate url", description = "암호화된 링크 검증")
    public ResponseEntity<UserDto> validateUrl(@RequestParam String link) {
        Integer userId = inquiryService.validateUrl(link);
        System.out.println("유저 아이디: " + userId);
        return ResponseEntity.ok(inquiryService.getUserInquiryNickname(userId));
    }
}
