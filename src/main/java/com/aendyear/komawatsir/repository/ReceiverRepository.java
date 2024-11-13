package com.aendyear.komawatsir.repository;

import com.aendyear.komawatsir.entity.Receiver;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReceiverRepository extends JpaRepository<Receiver, Integer> {

    Optional<Receiver> findBySenderIdAndReceiverUserIdAndYear(Integer senderId, Integer receiverId, String year);

    List<Receiver> findBySenderIdAndYearAndIsDeletedIsFalse(Integer senderId, String year);
}
