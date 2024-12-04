package com.aendyear.komawatsir.controller;

import com.aendyear.komawatsir.entity.Design;
import com.aendyear.komawatsir.service.DesignService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/designs")
public class DesignController {

    @Autowired
    private DesignService designService;

    @PostMapping
    @Operation(summary = "add design", description = "디자인 추가")
    public ResponseEntity<Design> addDesign(@PathVariable Integer userId) {
        return ResponseEntity.ok(designService.addDesign(userId));
    }

    @PutMapping("/{designId}/{isFront}/{imageId}")
    @Operation(summary = "change background", description = "background 혹은 thumbnail 변경, isFront == true {background}")
    public ResponseEntity<Design> putImage(@PathVariable Integer userId, @PathVariable Integer designId, @PathVariable Boolean isFront, @PathVariable Integer imageId) {
        return ResponseEntity.ok(designService.putImage(userId, designId, isFront, imageId));
    }

    @PutMapping("/font/{fontId}/{fontSize}/{fontColor}")
    @Operation(summary = "change font", description = "폰트 변경")
    public ResponseEntity<Design> changeFont(@PathVariable Integer fontId, @PathVariable String fontSize, @PathVariable String fontColor, @PathVariable Integer userId) {
        return ResponseEntity.ok(designService.changeFont(fontId, fontSize, fontColor, userId));
    }
}
