package com.aendyear.komawatsir.repository;

import com.aendyear.komawatsir.entity.InquiryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryItemRepository extends JpaRepository<InquiryItem, Integer> {

    List <InquiryItem> findByInquiryId(Integer inquiryId);
}
