package com.aendyear.komawatsir.repository.dsl;

import com.aendyear.komawatsir.dto.ReceiverDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReceiverRepositoryDSL {

    Page<ReceiverDto> findReceiverWithPaging(Integer userId, String nextYear, Pageable pageable,
                                             boolean pending, boolean progressing, boolean completed);
}
