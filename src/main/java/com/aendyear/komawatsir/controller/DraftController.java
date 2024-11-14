package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.dto.DraftDto;
import com.aendyear.komawatsir.entity.Draft;
import com.aendyear.komawatsir.service.DraftService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/drafts")
public class DraftController {

    @Autowired
    private DraftService draftService;

    @PostMapping
    @Operation(summary = "add draft", description = "초안 추가(등록)")
    public ResponseEntity<Draft> postAddDraft(@PathVariable Integer userId, @RequestBody DraftDto dto) {
        return ResponseEntity.ok(draftService.postAddDraft(userId, dto));
    }

    @GetMapping("/{draftId}")
    @Operation(summary = "show single draft", description = "단일 초안 조회")
    public ResponseEntity<DraftDto> getShowDraft(@PathVariable Integer userId, @PathVariable Integer draftId) {
        return ResponseEntity.ok(draftService.getShowDraft(userId, draftId));
    }

    @GetMapping
    @Operation(summary = "show all draft", description = "초안 목록 조회")
    public ResponseEntity<List<DraftDto>> getAllDrafts(@PathVariable Integer userId) {
        return ResponseEntity.ok(draftService.getAllDrafts(userId));
    }

    @DeleteMapping("/{draftId}")
    @Operation(summary = "remove draft", description = "초안 삭제")
    public ResponseEntity<Integer> deleteDraft(@PathVariable Integer userId, @PathVariable Integer draftId) {
        return ResponseEntity.ok(draftService.deleteDraft(userId, draftId));
    }
}
