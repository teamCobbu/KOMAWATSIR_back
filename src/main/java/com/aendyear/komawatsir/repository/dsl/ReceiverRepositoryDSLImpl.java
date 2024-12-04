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

        // Receiver 조회
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

        // Post 데이터 처리
        result.forEach(res -> {
            List<Post> posts = queryFactory.selectFrom(post)
                    .where(
                            post.receiverId.eq(res.getId())
                    )
                    .fetch();

            if (!posts.isEmpty()) {
                // Post가 있을 경우 데이터 설정
                Post postEntity = posts.get(0);
                res.setPostStatus(postEntity.getStatus());
                res.setPostContents(postEntity.getContents());
            } else {
                // Post가 없을 경우 기본값 설정
                res.setPostStatus(PostStatus.PENDING);
                res.setPostContents(""); // 기본 내용은 빈 문자열
            }
        });

        // Post 상태 필터링
        result = result.stream()
                .filter(res -> {
                    PostStatus status = res.getPostStatus();
                    return (pending && status == PostStatus.PENDING)
                            || (progressing && status == PostStatus.PROGRESSING)
                            || (completed && status == PostStatus.COMPLETED);
                })
                .collect(Collectors.toList());

        return result;
    }
}
