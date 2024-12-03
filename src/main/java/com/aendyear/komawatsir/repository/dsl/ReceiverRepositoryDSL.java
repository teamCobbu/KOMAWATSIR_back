package com.aendyear.komawatsir.repository.dsl;

import com.aendyear.komawatsir.dto.ReceiverDto;
import com.aendyear.komawatsir.type.PostStatus;

import java.util.List;

public interface ReceiverRepositoryDSL {

    List<ReceiverDto> findBySenderIdAndYearAndIsDeletedIsFalse(Integer userId, String nextYear,  boolean pending, boolean progressing, boolean completed);
}
