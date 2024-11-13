package com.aendyear.komawatsir.repository;

import com.aendyear.komawatsir.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InquiryRepository extends JpaRepository<Inquiry, Integer> {
    Optional<Inquiry> findByUserIdAndYear(Integer userId, String year);
}
