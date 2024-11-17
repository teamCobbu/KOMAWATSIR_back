package com.aendyear.komawatsir.repository;

import com.aendyear.komawatsir.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {
    List<Post> findByReceiverId(Integer receiverId);
}
