package com.aendyear.komawatsir.repository;

import com.aendyear.komawatsir.entity.ReceiverQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReceiverQuestionRepository extends JpaRepository<ReceiverQuestion, Integer> {

    List<ReceiverQuestion> findByReceiverId(Integer receiverId);
}
