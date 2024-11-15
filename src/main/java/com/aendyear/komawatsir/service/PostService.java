package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.PostDesignDto;
import com.aendyear.komawatsir.dto.PostDto;
import com.aendyear.komawatsir.entity.*;
import com.aendyear.komawatsir.repository.*;
import com.aendyear.komawatsir.type.PostStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class PostService {
    String year = String.valueOf(LocalDate.now().getYear());
    String nextYear = String.valueOf(LocalDate.now().getYear() + 1);

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ReceiverRepository receiverRepository;

    @Autowired
    private DesignRepository designRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FontRepository fontRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Value("${openai.api-key}")
    private String openaiApiKey;


    // 연도별 받은 연하장
    public List<PostDesignDto> getShowCard(Integer receiverUserId, String year) {
        List<PostDesignDto> result = new ArrayList<>();

        receiverRepository.findByReceiverUserIdAndYear(receiverUserId, year).forEach(receiver -> {
            List<Post> posts = postRepository.findByReceiverIdAndYearAndStatusNot(receiver.getId(), year, PostStatus.DELETED);

            posts.forEach(post -> {
                PostDesignDto postDesignDto = new PostDesignDto();
                postDesignDto.setPostId(post.getId());
                postDesignDto.setSenderId(post.getSenderId());
                postDesignDto.setReceiverId(post.getReceiverId());
                postDesignDto.setSenderNickname(post.getSenderNickname());
                postDesignDto.setContents(post.getContents());
                postDesignDto.setYear(post.getYear());

                Optional<Design> designs = designRepository.findByUserIdAndYear(post.getSenderId(), post.getYear());
                if (designs.isPresent()) {
                    Design design = designs.get();

                    // thumbnail
                    Optional<Image> thumbnail = imageRepository.findById(design.getThumbnailId());
                    thumbnail.ifPresent(image -> postDesignDto.setThumbnailPic(image.getPic()));

                    // background
                    Optional<Image> background = imageRepository.findById(design.getBackgroundId());
                    background.ifPresent(image -> postDesignDto.setBackgroundPic(image.getPic()));

                    // font
                    Optional<Font> fonts = fontRepository.findById(design.getFontId());
                    fonts.ifPresent(font -> {
                        postDesignDto.setFontSize(font.getSize());
                        postDesignDto.setFontColor(font.getColor());
                        postDesignDto.setFontUrl(font.getUrl());
                    });
                }

                result.add(postDesignDto);
            });
        });
        return result;
    }

    // gpt 통신으로 연하장 내용 생성하기
    // todo : prompt 에 조건 추가하기 (예: 50글자 이내 등)
    public String getUseGpt(String prompt) {
        WebClient webClient = WebClient.builder()
                .baseUrl("https://api.openai.com/v1/chat/completions")
                .defaultHeader("Authorization", "Bearer " + openaiApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();

        String response = webClient.post()
                .bodyValue("{\"model\": \"gpt-3.5-turbo\", \"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]}")
                .retrieve()
                .bodyToMono(String.class)
                .block();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);

            // 반환값 JSON 파싱
            JsonNode contentNode = rootNode
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content");

            return contentNode.asText();

        } catch (Exception e) {
            System.out.println("getUseGpt ERROR : " + e.getMessage());
        }

        return null;
    }

    // 연하장 임시 저장 혹은 저장 (수정 겸용)
    @Transactional
    public Post postAddPost(String status, PostDto dto) {
        Post post = Mapper.toEntity(dto);

        // 신청받는 설문에 등록한 닉네임 가져오기
        String inquiryNickname;
        Optional<Inquiry> inquiry = inquiryRepository.findByUserIdAndYear(dto.getSenderId(), nextYear);
        if (inquiry.isPresent()) {
            inquiryNickname = inquiry.get().getNickname();
            post.setSenderNickname(inquiryNickname);
        }
        post.setYear(nextYear);


        if (status.equals("progressing")) {
            post.setStatus(PostStatus.PROGRESSING);
        } else if (status.equals("completed")) {
            post.setStatus(PostStatus.COMPLETED);
        }

        return postRepository.save(post);
    }

    // 연하장 단일 조회
    public PostDto getSinglePost(Integer postId) {
        PostDto postDto = new PostDto();

        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            postDto = Mapper.toDto(post.get());
        }

        return postDto;
    }

    // 연하장 삭제
    @Transactional
    public Integer patchDeletePost(Integer postId) {
        Optional<Post> post = postRepository.findById(postId);
        if (post.isPresent()) {
            post.get().setStatus(PostStatus.DELETED);
            postRepository.save(post.get());
        }
        return postId;
    }
}
