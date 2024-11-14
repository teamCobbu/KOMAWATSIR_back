package com.aendyear.komawatsir.repository;

import com.aendyear.komawatsir.entity.Receiver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReceiverRepository extends JpaRepository<Receiver, Integer> {

    List<Receiver> findBySenderIdAndYearAndIsDeletedIsFalse(Integer senderId, String year);

    List<Receiver> findByReceiverUserIdAndYear(Integer receiverUserId, String year);
}
