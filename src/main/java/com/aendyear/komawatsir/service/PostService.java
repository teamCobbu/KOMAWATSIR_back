package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.PostDesignDto;
import com.aendyear.komawatsir.entity.Design;
import com.aendyear.komawatsir.entity.Font;
import com.aendyear.komawatsir.entity.Image;
import com.aendyear.komawatsir.entity.Post;
import com.aendyear.komawatsir.repository.*;
import com.aendyear.komawatsir.type.PostStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class PostService {

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

}
