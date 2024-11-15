package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.DraftDto;
import com.aendyear.komawatsir.entity.Draft;
import com.aendyear.komawatsir.repository.DraftRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class DraftService {

    String year = String.valueOf(LocalDate.now().getYear());
    String nextYear = String.valueOf(LocalDate.now().getYear() + 1);

    @Autowired
    private DraftRepository draftRepository;

    // 초안 추가(등록) 하기
    @Transactional
    public Draft postAddDraft(Integer userId, DraftDto dto) {
        return draftRepository.save(Mapper.toEntity(dto));
    }

    // 단일 초안 조회하기
    public DraftDto getShowDraft(Integer userId, Integer draftId) {
        DraftDto dto = new DraftDto();

        Optional<Draft> draft = draftRepository.findById(draftId);

        if (draft.isPresent()) {
            dto = Mapper.toDto(draft.get());
        }

        return dto;
    }

    // 초안 목록 조회하기
    public List<DraftDto> getAllDrafts(Integer userId) {
        return draftRepository.findByUserId(userId).stream().map(Mapper::toDto).toList();
    }

    // 초안 삭제하기
    @Transactional
    public Integer deleteDraft(Integer userId, Integer draftId) {
        draftRepository.deleteById(draftId);
        return draftId;
    }


}
