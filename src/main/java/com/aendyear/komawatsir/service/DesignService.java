package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.entity.Design;
import com.aendyear.komawatsir.repository.DesignRepository;
import com.aendyear.komawatsir.type.FontColor;
import com.aendyear.komawatsir.type.FontSize;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Optional;

@Service
@Validated
public class DesignService {

    String year = String.valueOf(LocalDate.now().getYear());
    String nextYear = String.valueOf(LocalDate.now().getYear() + 1);

    @Autowired
    private DesignRepository designRepository;

    // 배경 / 썸네일 변경
    @Transactional
    public Design putImage(Integer userId, Integer designId, Boolean isFront, Integer imageId) {
        Design design = new Design();
        Optional<Design> designs = designRepository.findById(designId);
        if (designs.isPresent()) {
            design = designs.get();
            if (isFront) {
                // 앞면 (background)
                design.setBackgroundId(imageId);
            } else {
                // 뒷면 (thumbnail)
                design.setThumbnailId(imageId);
            }
            design = designRepository.save(design);
        }

        return design;
    }

    // 디자인 추가
    @Transactional
    public Design addDesign(Integer userId) {
        Design design = Design.builder()
                .userId(userId)
                .backgroundId(11)
                .thumbnailId(16)
                .fontId(1)
                .fontColor(FontColor.white)
                .fontSize(FontSize.defaultSize)
                .year(nextYear)
                .build();
        design = designRepository.save(design);
        return design;
    }

    // 폰트 변경하기
    @Transactional
    public Design changeFont(Integer fontId, String fontSize, String fontColor, Integer userId) {
        Design result = new Design();

        FontSize finalFontSize = fontSize.equals("defaultSize") ? FontSize.defaultSize : FontSize.bigSize;
        FontColor finalFontColor = fontColor.equals("white") ? FontColor.white : FontColor.black;

        Optional<Design> design = designRepository.findByUserIdAndYear(userId, nextYear);
        design.ifPresent(selectDesign -> selectDesign.setFontId(fontId));
        if (design.isPresent()) {
            design.get().setFontId(fontId);
            design.get().setFontSize(finalFontSize);
            design.get().setFontColor(finalFontColor);

            result = designRepository.save(design.get());
        }

        return result;
    }
}
