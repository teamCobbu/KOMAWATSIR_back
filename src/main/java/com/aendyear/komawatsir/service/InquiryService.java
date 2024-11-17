package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.InquiryItemDto;
import com.aendyear.komawatsir.entity.Inquiry;
import com.aendyear.komawatsir.entity.InquiryItem;
import com.aendyear.komawatsir.repository.InquiryItemRepository;
import com.aendyear.komawatsir.repository.InquiryRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Validated
public class InquiryService {

    String year = String.valueOf(LocalDate.now().getYear());
    String nextYear = String.valueOf(LocalDate.now().getYear() + 1);

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private InquiryItemRepository inquiryItemRepository;

    @Value("${komawatsir-pwd}")
    private String secretKey;

    private static String SECRET_KEY = "";

    @PostConstruct
    public void init() {
        SECRET_KEY = this.secretKey;
    }

    // 신청 질문 목록 불러오기
    public List<InquiryItemDto> getQuestionList(Integer userId) {
        List<InquiryItemDto> result = new ArrayList<>();
        Optional<Inquiry> inquiry = inquiryRepository.findByUserIdAndYear(userId, nextYear);
        System.out.println("nextYear : " + nextYear);
        if (inquiry.isPresent()) {
            result = inquiryItemRepository.findByInquiryId(inquiry.get().getUserId()).stream().map(Mapper::toDto).collect(Collectors.toList());
        }
        return result;
    }

    // 닉네임 등록하기
    @Transactional
    public Inquiry postQuestion(Integer userId, String nickname) {
        Inquiry inquiry = new Inquiry();

        inquiry.setUserId(userId);
        inquiry.setYear(nextYear);
        inquiry.setNickname(nickname);

        inquiry = inquiryRepository.save(inquiry);

        return inquiry;
    }

    // 신청 질문 추가하기
    @Transactional
    public InquiryItem postInsertQuestion(Integer userId, InquiryItemDto dto) {
        InquiryItem result = new InquiryItem();

        Optional<Inquiry> inquiry = inquiryRepository.findByUserIdAndYear(userId, nextYear);
        if (inquiry.isPresent()) {
            dto.setInquiryId(inquiry.get().getId());
            result = inquiryItemRepository.save(Mapper.toEntity(dto));
        }

        return result;
    }

    // 질문 수정하기
    @Transactional
    public InquiryItem putUpdateQuestion(InquiryItemDto dto) {
        return inquiryItemRepository.save(Mapper.toEntity(dto));
    }

    // 질문 삭제하기
    @Transactional
    public Integer deleteRemoveQuestion(InquiryItemDto dto) {

        Integer deleteId = dto.getId();
        inquiryItemRepository.deleteById(deleteId);

        return deleteId;
    }

    // url 추출하기
    public String getUrl(Integer userId) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(secretKey);

            byte[] hmacBytes = mac.doFinal(String.valueOf(userId).getBytes());

            return Base64.getUrlEncoder().withoutPadding().encodeToString(hmacBytes);
        } catch (Exception e) {
            System.out.println("getUrl ERROR : " + e.getMessage());
            return null;
        }
    }

    // url 검증하기
    public Boolean validateUrl(Integer userId, String url) {
        String userIdHmac = getUrl(userId);

       if (userIdHmac == null || url == null || userIdHmac.length() != url.length()) {
           return false;
       }

       int result = 0;
       for (int i = 0; i < userIdHmac.length(); i++) {
           result |= userIdHmac.charAt(i) ^ url.charAt(i);
       }

        return result == 0;
    }
}
