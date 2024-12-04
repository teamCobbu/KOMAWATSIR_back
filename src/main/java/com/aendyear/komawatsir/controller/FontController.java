package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.dto.FontDto;
import com.aendyear.komawatsir.service.FontService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fonts")
public class FontController {

    @Autowired
    private FontService fontService;

    @GetMapping("/{fontId}")
    @Operation(summary = "load font url", description = "단일 폰트 조회 (url 가져오기)")
    public ResponseEntity<FontDto> getFontUrl(@PathVariable Integer fontId) {
        return ResponseEntity.ok(fontService.getFontUrl(fontId));
    }

    @GetMapping
    @Operation(summary = "load font list", description = "폰트 목록 조회")
    public ResponseEntity<List<FontDto>> getFontList() {
        return ResponseEntity.ok(fontService.getFontList());
    }
}
