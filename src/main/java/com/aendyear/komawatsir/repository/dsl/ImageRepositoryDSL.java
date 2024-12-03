package com.aendyear.komawatsir.repository.dsl;

import com.aendyear.komawatsir.entity.Image;
import com.aendyear.komawatsir.type.ImageCategory;
import com.aendyear.komawatsir.type.SourceType;

import java.util.List;

public interface ImageRepositoryDSL {
    List<Image> findByCategoryAndEtc(ImageCategory category, SourceType sourceType, Boolean isFront, Integer userId);
}
