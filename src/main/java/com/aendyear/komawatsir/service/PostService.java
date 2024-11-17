package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.PostDto;
import com.aendyear.komawatsir.entity.Post;
import com.aendyear.komawatsir.entity.Receiver;
import com.aendyear.komawatsir.repository.PostRepository;
import com.aendyear.komawatsir.repository.ReceiverRepository;
import com.aendyear.komawatsir.type.PostStatus;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Year;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ReceiverRepository receiverRepository;

    // 연하장 생성
    public Post createPost(@Valid PostDto postDto) {
        // receiver에서 senderId와 receiverUserId를 가져옴
        Receiver receiver = receiverRepository.findById(postDto.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID"));

        Post post = Post.builder()
                .contents(postDto.getContents())
                .receiverId(receiver.getReceiverUserId())
                .senderId(receiver.getSenderId()) // 비회원일 경우 null로 저장
                .senderNickname(postDto.getSenderNickname()) // 닉네임 설정
                .status(PostStatus.PENDING) // Enum 기본값 설정
                .year(Year.now().getValue()) // 현재 연도 설정
                .build();

        return postRepository.save(post);
    }

    // 단일 연하장 조회
    public Optional<Post> getPostById(Integer id) {
        return postRepository.findById(id);
    }

    // 수신인별 받은 연하장 조회
    public List<Post> getPostsByReceiverId(Integer receiverId) {
        return postRepository.findByReceiverId(receiverId);
    }

    // 연하장 상태 수정
    public Post updatePostStatus(Integer id, String status) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid Post ID"));

        try {
            PostStatus postStatus = PostStatus.valueOf(status);
            post.setStatus(postStatus);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        };
        return postRepository.save(post);
    }
}
