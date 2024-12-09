package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.PostDesignDto;
import com.aendyear.komawatsir.dto.PostDto;
import com.aendyear.komawatsir.entity.*;
import com.aendyear.komawatsir.repository.*;
import com.aendyear.komawatsir.type.ImageCategory;
import com.aendyear.komawatsir.type.PostStatus;
import com.aendyear.komawatsir.type.SourceType;
import jakarta.transaction.Transactional;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @Autowired
    private OpenAiChatModel openAiChatModel;

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
                        postDesignDto.setFontUrl(font.getUrl());
                    });
                }

                result.add(postDesignDto);
            });
        });
        return result;
    }

    // 유저가 받은 전체 연하장
    public List<PostDesignDto> getCardsByUser(Integer receiverUserId) {
        List<PostDesignDto> result = new ArrayList<>();

        receiverRepository.findByReceiverUserId(receiverUserId).forEach(receiver -> {
            List<Post> posts = postRepository.findByReceiverIdAndStatusNot(receiver.getId(), PostStatus.DELETED);

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
                        postDesignDto.setFontUrl(font.getUrl());
                        postDesignDto.setFontName(font.getName());
                    });
                }
                result.add(postDesignDto);
            });
        });
        return result;
    }

    // gpt 통신으로 연하장 내용 생성하기
    public String getUseGpt(String prompt) {
        String request = prompt + " " + nextYear + "년 연하장, 한글로, 100글자";
        return openAiChatModel.call(request);
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


    // 연하장 작성 여부 조회
    public Integer getPostCheck(Integer userId, Integer receiverId) {
        int result = 0;
        Optional<Post> post = postRepository.findBySenderIdAndReceiverIdAndYear(userId, receiverId, nextYear);
        if (post.isPresent()) {
            result = post.get().getId();
        }
        return result;
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

    public PostDesignDto getPostDesign(Integer userId) {
        PostDesignDto postDesignDto = new PostDesignDto();

           Optional<Design> design =  designRepository.findByUserIdAndYear(userId, nextYear);
           if(design.isPresent()) {
              Optional<Image> image = imageRepository.findById(design.get().getBackgroundId());
              image.ifPresent(value -> {
                  postDesignDto.setBackgroundPic(value.getPic());
                  postDesignDto.setBackgroundId(value.getId());
              });
              image = imageRepository.findById(design.get().getThumbnailId());
              image.ifPresent(value -> {
                  postDesignDto.setThumbnailPic(value.getPic());
                  postDesignDto.setThumbnailId(value.getId());
              });

              postDesignDto.setDesignId(design.get().getId());
              postDesignDto.setFontSize(design.get().getFontSize());
              postDesignDto.setFontColor(design.get().getFontColor());

              Optional<Font> font = fontRepository.findById(design.get().getFontId());
              font.ifPresent(value -> {
                  postDesignDto.setFontUrl(value.getUrl());
                  postDesignDto.setFontId(value.getId());
                  postDesignDto.setFontName(value.getName());
              });

           }
        return postDesignDto;
    }


    // todo: 테스트용 (추후 삭제)
    // 연도별 받은 연하장
    public List<PostDesignDto> getAllCards() {
        List<PostDesignDto> result = new ArrayList<PostDesignDto>();

            List<Post> posts = postRepository.findAll();

            posts.forEach(post -> {
                PostDesignDto postDesignDto = new PostDesignDto();
                postDesignDto.setPostId(post.getId());
                postDesignDto.setSenderId(post.getSenderId());
                postDesignDto.setReceiverId(post.getReceiverId());
                postDesignDto.setSenderNickname(post.getSenderNickname());
                postDesignDto.setContents(post.getContents());
                postDesignDto.setYear(post.getYear());
                postDesignDto.setBackgroundPic(post.getImageUrl());

                Optional<Design> designs = designRepository.findByUserIdAndYear(post.getSenderId(), post.getYear());
                if (designs.isPresent()) {
                    Design design = designs.get();

                    // thumbnail
                    Optional<Image> thumbnail = imageRepository.findById(design.getThumbnailId());
                    thumbnail.ifPresent(image -> postDesignDto.setThumbnailPic(image.getPic()));

                    // font
                    Optional<Font> fonts = fontRepository.findById(design.getFontId());
                    fonts.ifPresent(font -> {
                        postDesignDto.setFontUrl(font.getUrl());
                        postDesignDto.setFontName(font.getName());
                    });
                }
                result.add(postDesignDto);
            });
        return result;
    }
}
