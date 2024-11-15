package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.ReceiverDto;
import com.aendyear.komawatsir.dto.ReceiverQuestionDto;
import com.aendyear.komawatsir.entity.InquiryItem;
import com.aendyear.komawatsir.entity.Receiver;
import com.aendyear.komawatsir.entity.User;
import com.aendyear.komawatsir.repository.InquiryItemRepository;
import com.aendyear.komawatsir.repository.ReceiverQuestionRepository;
import com.aendyear.komawatsir.repository.ReceiverRepository;
import com.aendyear.komawatsir.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class ReceiverService {

    String year = String.valueOf(LocalDate.now().getYear());
    String nextYear = String.valueOf(LocalDate.now().getYear() + 1);

    @Autowired
    private ReceiverRepository receiverRepository;

    @Autowired
    private ReceiverQuestionRepository receiverQuestionRepository;

    @Autowired
    private InquiryItemRepository inquiryItemRepository;

    @Autowired
    private UserRepository userRepository;

    // 중복 신청 여부 확인하기
    public Boolean duplicationCheck(Integer senderId, String tel) {
        boolean b = false;

        Optional<Receiver> receiver = receiverRepository.findBySenderIdAndTelAndYear(senderId, tel, nextYear);
        if (receiver.isPresent()) {
            b = true;
        }

        return b;
    }

    // 수신인 추가하기
    @Transactional
    public Receiver postAddReceiver(Integer senderId, ReceiverDto dto) {
        Receiver result = new Receiver();
        Integer receiverUserId = null;

        // 비회원 신청
        if (dto.getReceiverUserId() == null) {
            Optional<User> user = userRepository.findByTel(dto.getTel());

            // user 에 등록되지 않은 전화번호 -> user 테이블에 전화번호만 추가
            if (user.isEmpty()) {
                User userResult = userRepository.save(User.builder().tel(dto.getTel()).build());
                receiverUserId = userResult.getId();
            } else {
                receiverUserId = user.get().getId();
            }
        } else {
            receiverUserId = dto.getReceiverUserId();
        }

        dto.setSenderId(senderId);
        dto.setReceiverUserId(receiverUserId);

        dto.setYear(nextYear);
        dto.setIsDeleted(false);

        result = receiverRepository.save(Mapper.toEntity(dto));

        return result;
    }

    // 수신인 설문 응답 조회하기
    public List<ReceiverQuestionDto> getReceiverQuestion(Integer senderId, Integer receiverId) {

        List<ReceiverQuestionDto> result = new ArrayList<>();
        Optional<Receiver> receiver = receiverRepository.findById(receiverId);

        if (receiver.isPresent()) {
            result = receiverQuestionRepository.findByReceiverId(receiver.get().getId()).stream().map(Mapper::toDto).toList();

            result.forEach(dto -> {
                Optional<InquiryItem> inquiryItem = inquiryItemRepository.findById(dto.getInquiryItemId());
                inquiryItem.ifPresent(item -> {
                    dto.setQuestion(item.getQuestion());
                });
            });
        }

        return result;
    }

    // 수신인 목록 조회하기
    public List<ReceiverDto> getReceiverList(Integer userId) {
        List<ReceiverDto> result = new ArrayList<>();
        result = receiverRepository.findBySenderIdAndYearAndIsDeletedIsFalse(userId, nextYear).stream().map(Mapper::toDto).toList();

        return result;
    }
}
