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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class InquiryService {

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private InquiryItemRepository inquiryItemRepository;

    // 신청 질문 목록 불러오기
    public List<InquiryItemDto> getQuestionList(Integer userId, String year) {
        List<InquiryItemDto> result = new ArrayList<>();
        Optional<Inquiry> inquiry = inquiryRepository.findByUserIdAndYear(userId, year);
        if (inquiry.isPresent()) {
            result = inquiryItemRepository.findByInquiryId(inquiry.get().getUserId()).stream().map(Mapper::toDto).collect(Collectors.toList());
        }
        return result;
    }

    // 신청 질문 추가하기
    @Transactional
    public InquiryItem postInsertQuestion(Integer userId, String year, InquiryItemDto dto) {
        InquiryItem result = new InquiryItem();
        try {
            // 초기 등록인지 확인하기
            Optional<Inquiry> inquiry = inquiryRepository.findByUserIdAndYear(userId, year);

            // 초기 등록일 경우 inquiry 생성
            if (inquiry.isEmpty()) {
                Inquiry newInquiry = Inquiry.builder()
                        .userId(userId)
                        .year(year)
                        .build();
                inquiry = Optional.of(inquiryRepository.save(newInquiry));
            }

            dto.setInquiryId(inquiry.get().getId());
            result = inquiryItemRepository.save(Mapper.toEntity(dto));
        } catch (Exception e) {
            System.out.println("postInsertQuestion : " + e.getMessage());
        }
        return result;
    }

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
