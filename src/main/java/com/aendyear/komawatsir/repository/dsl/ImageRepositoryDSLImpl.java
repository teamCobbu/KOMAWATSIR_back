package com.aendyear.komawatsir.repository.dsl;

import com.aendyear.komawatsir.entity.Image;
import com.aendyear.komawatsir.entity.QImage;
import com.aendyear.komawatsir.type.ImageCategory;
import com.aendyear.komawatsir.type.SourceType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ImageRepositoryDSLImpl implements ImageRepositoryDSL {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Image> findByCategoryAndEtc(ImageCategory category, SourceType sourceType, Boolean isFront, Integer userId) {
        QImage image = QImage.image;

        return queryFactory.selectFrom(image)
                .where(
                        image.category.eq(category),
                        image.sourceType.eq(sourceType).and(image.isFront.eq(isFront)).or(image.userId.eq(userId))
                )
                .fetch();
    }
}
