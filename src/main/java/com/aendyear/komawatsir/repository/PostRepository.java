package com.aendyear.komawatsir.repository;

import com.aendyear.komawatsir.entity.Post;
import com.aendyear.komawatsir.type.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByReceiverIdAndYearAndStatusNot(Integer receiverId, String year, PostStatus status);
}
