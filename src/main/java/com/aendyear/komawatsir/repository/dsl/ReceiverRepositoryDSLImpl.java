package com.aendyear.komawatsir.repository.dsl;

import com.aendyear.komawatsir.dto.ReceiverDto;
import com.aendyear.komawatsir.entity.*;
import com.aendyear.komawatsir.service.Mapper;
import com.aendyear.komawatsir.type.PostStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReceiverRepositoryDSLImpl implements ReceiverRepositoryDSL {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ReceiverDto> findBySenderIdAndYearAndIsDeletedIsFalse(Integer userId, String nextYear, boolean pending, boolean progressing, boolean completed) {
        QReceiver receiver = QReceiver.receiver;
        QPost post = QPost.post;

        // 동적 필터 조건 추가
        BooleanBuilder postStatusCondition = new BooleanBuilder();
        if (pending) {
            postStatusCondition.or(post.status.eq(PostStatus.PENDING));
        }
        if (progressing) {
            postStatusCondition.or(post.status.eq(PostStatus.PROGRESSING));
        }
        if (completed) {
            postStatusCondition.or(post.status.eq(PostStatus.COMPLETED));
        }

        List<ReceiverDto> result = queryFactory.selectFrom(receiver)
                .where(
                        receiver.senderId.eq(userId),
                        receiver.year.eq(nextYear),
                        receiver.isDeleted.isFalse()
                )
                .fetch()
                .stream()
                .map(Mapper::toDto)
                .collect(Collectors.toList());

        result = result.stream()
                .filter(res -> {
                    List<Post> posts = queryFactory.selectFrom(post)
                            .where(
                                    post.receiverId.eq(res.getId()),
                                    postStatusCondition // 동적으로 추가된 필터 조건
                            )
                            .fetch();

                    if (!posts.isEmpty()) {
                        Post postEntity = posts.get(0);
                        res.setPostStatus(postEntity.getStatus());
                        res.setPostContents(postEntity.getContents());
                        return true;
                        // 필터 통과
                    } else {
                        return false;
                        // 조건에 맞는 Post가 없으면 제외
                    }
                })
                .collect(Collectors.toList());

        return result;
    }
}
