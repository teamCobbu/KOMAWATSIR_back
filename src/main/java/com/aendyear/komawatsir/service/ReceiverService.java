package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.ReceiverDto;
import com.aendyear.komawatsir.dto.ReceiverQuestionDto;
import com.aendyear.komawatsir.entity.InquiryItem;
import com.aendyear.komawatsir.entity.Receiver;
import com.aendyear.komawatsir.repository.InquiryItemRepository;
import com.aendyear.komawatsir.repository.ReceiverQuestionRepository;
import com.aendyear.komawatsir.repository.ReceiverRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.sql.Date;
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

    // 수신인 추가하기
    @Transactional
    public Receiver postAddReceiver(Integer senderId, ReceiverDto dto) {
        Receiver result = new Receiver();
        try {
            dto.setSenderId(senderId);
            result = receiverRepository.save(Mapper.toEntity(dto));
        } catch (Exception e) {
            System.out.println("postAddReceiver ERROR : " + e.getMessage());
        }
        return result;
    }

    // 수신인 설문 응답 조회하기
    public List<ReceiverQuestionDto> getReceiverQuestion(Integer senderId, Integer receiverId) {

        List<ReceiverQuestionDto> result = new ArrayList<>();
        Optional<Receiver> receiver = receiverRepository.findById(receiverId);

        if (receiver.isPresent()) {
            System.out.println("getReceiverQuestion SUCCESS");
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
