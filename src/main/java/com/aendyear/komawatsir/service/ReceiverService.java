package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.ReceiverAdderDto;
import com.aendyear.komawatsir.dto.ReceiverDto;
import com.aendyear.komawatsir.dto.ReceiverQuestionDto;
import com.aendyear.komawatsir.entity.InquiryItem;
import com.aendyear.komawatsir.entity.Receiver;
import com.aendyear.komawatsir.entity.ReceiverQuestion;
import com.aendyear.komawatsir.entity.User;
import com.aendyear.komawatsir.repository.*;
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

    @Autowired
    private PostRepository postRepository;


    // 중복 신청 여부 확인하기
    public Boolean duplicationCheck(Integer senderId, String tel) {
        boolean b = false;

        Optional<Receiver> receiver = receiverRepository.findBySenderIdAndTelAndYear(senderId, tel, nextYear);
        if (receiver.isPresent()) {
            b = true;
        }

        return b;
    }

    InquiryService inquiryService;

    // 수신인 신청: 수신인 추가 & 답변 등록
    @Transactional
    public Receiver postAddReceiver(Integer senderId, ReceiverAdderDto dto) {
        // 수신인 추가
        ReceiverDto receiverDto = dto.getReceiver();
        Receiver result = new Receiver();
        Integer receiverUserId = null;

        if (receiverDto.getReceiverUserId() == null) { // 비회원 신청
            Optional<User> user = userRepository.findByTel(receiverDto.getTel());
            if (user.isEmpty()) { // user 에 등록되지 않은 전화번호 -> user 테이블에 전화번호만 추가
                User userResult = userRepository.save(User.builder().tel(receiverDto.getTel()).build());
                receiverUserId = userResult.getId();
            } else {
                receiverUserId = user.get().getId();
            }
        } else { // 회원
            receiverUserId = receiverDto.getReceiverUserId();
        }
        receiverDto.setSenderId(senderId);
        receiverDto.setReceiverUserId(receiverUserId);
        receiverDto.setYear(nextYear);
        receiverDto.setIsDeleted(false);
        result = receiverRepository.save(Mapper.toEntity(receiverDto));
        Integer receiverId = result.getId();

        // 답변 등록: 직접 추가가 아닌 신청 시에만 (answers가 null이 아닐 때만)
        List<ReceiverQuestionDto> answers = dto.getAnswers();
        if(answers != null && !answers.isEmpty()){
            for (ReceiverQuestionDto answer : answers) {
                answer.setReceiverId(receiverId);
                ReceiverQuestion receiverQuestion = Mapper.toEntity(answer);
                receiverQuestionRepository.save(receiverQuestion);
            }
        }
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

    // 메모 수정하기
    public Receiver putEditMemo(Integer receiverId, String memo) {
        Receiver result = new Receiver();
        Optional<Receiver> receiver = receiverRepository.findById(receiverId);

            if (receiver.isPresent()) {
                receiver.get().setMemo(memo);
               result = receiverRepository.save(receiver.get());
            }

        return result;
    }

    // 수신인 목록 조회하기
    public List<ReceiverDto> getReceiverList(Integer userId, boolean pending, boolean progressing, boolean completed) {
        return receiverRepository.findBySenderIdAndYearAndIsDeletedIsFalse(userId, nextYear, pending, progressing, completed);
    }
}
