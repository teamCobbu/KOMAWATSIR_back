package com.aendyear.komawatsir.repository;

import com.aendyear.komawatsir.entity.Image;
import com.aendyear.komawatsir.type.ImageCategory;
import com.aendyear.komawatsir.type.SourceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Integer> {

    List<Image> findBySourceTypeAndCategoryAndIsFrontOrUserId(SourceType sourceType, ImageCategory category, Boolean isFront, Integer userId);

}
