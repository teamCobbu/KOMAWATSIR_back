package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.InquiryItemDto;
import com.aendyear.komawatsir.entity.Inquiry;
import com.aendyear.komawatsir.entity.InquiryItem;
import com.aendyear.komawatsir.repository.InquiryItemRepository;
import com.aendyear.komawatsir.repository.InquiryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class InquiryService {

    String year = String.valueOf(LocalDate.now().getYear());
    String nextYear = year + 1;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private InquiryItemRepository inquiryItemRepository;

    // 신청 질문 목록 불러오기
    public List<InquiryItemDto> getQuestionList(Integer userId) {
        List<InquiryItemDto> result = new ArrayList<>();
        Optional<Inquiry> inquiry = inquiryRepository.findByUserIdAndYear(userId, nextYear);
        if (inquiry.isPresent()) {
            result = inquiryItemRepository.findByInquiryId(inquiry.get().getUserId()).stream().map(Mapper::toDto).collect(Collectors.toList());
        }
        return result;
    }

    // 신청 질문 추가하기
    @Transactional
    public InquiryItem postInsertQuestion(Integer userId, InquiryItemDto dto) {
        InquiryItem result = new InquiryItem();
        try {
            Optional<Inquiry> inquiry = inquiryRepository.findByUserIdAndYear(userId, nextYear);
            if (inquiry.isPresent()) {
                dto.setInquiryId(inquiry.get().getId());
                result = inquiryItemRepository.save(Mapper.toEntity(dto));
            }
        } catch (Exception e) {
            System.out.println("postInsertQuestion : " + e.getMessage());
        }
        return result;
    }

    // 닉네임 등록하기
    @Transactional
    public Inquiry postQuestion(Integer userId, String nickname) {
        Inquiry inquiry = new Inquiry();
        try {

            // 초기 닉네임 등록과 동시에 inquiry 데이터 생성
            inquiry.setUserId(userId);
            inquiry.setYear(nextYear);
            inquiry.setNickname(nickname);

            inquiry = inquiryRepository.save(inquiry);
        } catch (Exception e) {
            System.out.println("postQuestion : " + e.getMessage());
        }
        return inquiry;
    }

    // 질문 수정하기
    @Transactional
    public InquiryItem putUpdateQuestion(InquiryItemDto dto) {
        InquiryItem result = new InquiryItem();
        try {
            result = inquiryItemRepository.save(Mapper.toEntity(dto));
        } catch (Exception e) {
            System.out.println("putUpdateQuestion : " + e.getMessage());
        }
        return result;
    }

    // 질문 삭제하기
    @Transactional
    public Integer deleteRemoveQuestion(InquiryItemDto dto) {
        Integer deleteId = null;
        try {
            deleteId = dto.getId();
            inquiryItemRepository.deleteById(deleteId);
        } catch (Exception e) {
            System.out.println("putUpdateQuestion : " + e.getMessage());
        }
        return deleteId;
    }
}
