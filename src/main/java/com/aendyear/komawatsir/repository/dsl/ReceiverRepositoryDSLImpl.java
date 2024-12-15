package com.aendyear.komawatsir.repository.dsl;

import com.aendyear.komawatsir.dto.ReceiverDto;
import com.aendyear.komawatsir.entity.Post;
import com.aendyear.komawatsir.entity.QPost;
import com.aendyear.komawatsir.entity.QReceiver;
import com.aendyear.komawatsir.type.PostStatus;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReceiverRepositoryDSLImpl implements ReceiverRepositoryDSL {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<ReceiverDto> findReceiverWithPaging(
            Integer userId,
            String nextYear,
            Pageable pageable,
            boolean pending,
            boolean progressing,
            boolean completed
    ) {
        QReceiver receiver = QReceiver.receiver;
        QPost post = QPost.post;

        // 필터 조건 추가
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

        // 기본 Receiver QueryDSL 쿼리 생성
        JPQLQuery<ReceiverDto> query = queryFactory.select(Projections.fields(
                        ReceiverDto.class,
                        receiver.id,
                        receiver.senderId,
                        receiver.nickname,
                        receiver.tel,
                        receiver.memo,
                        receiver.year,
                        receiver.isDeleted
                ))
                .from(receiver)
                .where(
                        receiver.senderId.eq(userId),
                        receiver.year.eq(nextYear),
                        receiver.isDeleted.isFalse()
                )
                .orderBy(receiver.id.asc());

        // Receiver 데이터 조회
        List<ReceiverDto> results = query.fetch();

        // Post 데이터 처리 및 필터링
        List<ReceiverDto> filteredResults = results.stream().filter(res -> {
            // Post 상태 확인
            List<Post> posts = queryFactory.selectFrom(post)
                    .where(
                            post.receiverId.eq(res.getId())
                    )
                    .fetch();

            if (!posts.isEmpty()) {
                // Post가 존재하는 경우 상태와 내용을 설정
                Post postEntity = posts.get(0);
                res.setPostStatus(postEntity.getStatus());
                res.setPostContents(postEntity.getContents());

                // 해당 상태가 체크된 경우에만 포함
                return (postEntity.getStatus() == PostStatus.PENDING && pending)
                        || (postEntity.getStatus() == PostStatus.PROGRESSING && progressing)
                        || (postEntity.getStatus() == PostStatus.COMPLETED && completed);
            } else {
                // Post가 없는 경우
                if (pending) {
                    res.setPostStatus(PostStatus.PENDING);
                    res.setPostContents("");
                    return true; // PENDING이 체크된 경우 포함
                } else {
                    return false; // PENDING이 체크 해제된 경우 제외
                }
            }
        }).collect(Collectors.toList());

        // 페이징 처리
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filteredResults.size());
        List<ReceiverDto> pagedResults = filteredResults.subList(start, end);

        // Page 객체 반환
        return new PageImpl<>(pagedResults, pageable, filteredResults.size());
    }
}


