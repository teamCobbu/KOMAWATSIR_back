package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.FontDto;
import com.aendyear.komawatsir.entity.Design;
import com.aendyear.komawatsir.entity.Font;
import com.aendyear.komawatsir.repository.DesignRepository;
import com.aendyear.komawatsir.repository.FontRepository;
import com.aendyear.komawatsir.type.FontColor;
import com.aendyear.komawatsir.type.FontSize;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class FontService {

    String year = String.valueOf(LocalDate.now().getYear());
    String nextYear = String.valueOf(LocalDate.now().getYear() + 1);

    @Autowired
    private FontRepository fontRepository;

    @Autowired
    private DesignRepository designRepository;

    // 단일 폰트 상세 보기 (url 가져오기)
    public FontDto getFontUrl(Integer fontId) {
        FontDto result = new FontDto();

        Optional<Font> font = fontRepository.findById(fontId);
        if(font.isPresent()) {
            result = Mapper.toDto(font.get());
        }

        return result;
    }

    // 폰트 목록 불러오기
    public List<FontDto> getFontList() {
        return fontRepository.findAll().stream().map(Mapper::toDto).toList();
    }

    // 폰트 변경하기
    @Transactional
    public Design changeFont(Integer fontId, String fontSize, String fontColor, Integer userId) {
        Design result = new Design();

        FontSize finalFontSize = fontSize.equals("default") ? FontSize.defaultSize : FontSize.bigSize;
        FontColor finalFontColor = fontColor.equals("white") ? FontColor.white : FontColor.black;

        Optional<Design> design = designRepository.findByUserIdAndYear(userId, nextYear);
        design.ifPresent(selectDesign -> selectDesign.setFontId(fontId));
        if(design.isPresent()) {
            design.get().setFontId(fontId);
            design.get().setFontSize(finalFontSize);
            design.get().setFontColor(finalFontColor);

            result = designRepository.save(design.get());
        }

        return result;
    }


}
