package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.InquiryItemDto;
import com.aendyear.komawatsir.dto.UserDto;
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

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
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
        if (inquiry.isPresent()) {
            result = inquiryItemRepository.findByInquiryId(inquiry.get().getId()).stream().map(Mapper::toDto).collect(Collectors.toList());
        }
        return result;
    }

    // 닉네임 여부 확인하기
    public boolean getCheckNickname(Integer userId) {
        return inquiryRepository.findByUserIdAndYear(userId, nextYear).isPresent();
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
    public Integer deleteRemoveQuestion(Integer deleteId) {
        inquiryItemRepository.deleteById(deleteId);
        return deleteId;
    }

    // url 추출하기
    public String getUrl(Integer userId) {
        try {
            String data = userId + ":" + System.currentTimeMillis();
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            System.out.println("링크: " + Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(encrypted);
        } catch (Exception e) {
            System.out.println("encryptUserId ERROR : " + e.getMessage());
            return null;
        }
    }

    // url 검증하기
    public Integer validateUrl(String encryptedUrl) {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decryptedBytes = cipher.doFinal(Base64.getUrlDecoder().decode(encryptedUrl));
            String decryptedData = new String(decryptedBytes, StandardCharsets.UTF_8);

            System.out.println("Decrypted Data: " + decryptedData);

            String[] parts = decryptedData.split(":");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid decrypted data format");
            }
            return Integer.parseInt(parts[0]); // 사용자 ID 반환
        } catch (Exception e) {
            System.out.println("decryptUserId ERROR : " + e.getMessage());
            return null;
        }
    }

    public UserDto getUserInquiryNickname(Integer userId) {
        UserDto user = new UserDto();
        Optional<Inquiry> inquiry = inquiryRepository.findByUserIdAndYear(userId, nextYear);
        System.out.println("Inquiry: " + inquiry.isPresent());
        if (inquiry.isPresent()) {
            user.setId(userId);
            user.setName(inquiry.get().getNickname());
        }
        return user;
    }
}
