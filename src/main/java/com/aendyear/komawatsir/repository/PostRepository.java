package com.aendyear.komawatsir.repository;

import com.aendyear.komawatsir.entity.Post;
import com.aendyear.komawatsir.type.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByReceiverIdAndYearAndStatusNot(Integer receiverId, String year, PostStatus status);
    Optional<Post> findByReceiverId(Integer receiverId);
    Optional<Post> findBySenderIdAndReceiverIdAndYear(Integer userId, Integer receiverId, String year);
}
