package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.PostDesignDto;
import com.aendyear.komawatsir.dto.PostDto;
import com.aendyear.komawatsir.entity.Font;
import com.aendyear.komawatsir.entity.Image;
import com.aendyear.komawatsir.entity.*;
import com.aendyear.komawatsir.repository.*;
import com.aendyear.komawatsir.type.PostStatus;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.transaction.Transactional;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
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

    @Autowired
    private OpenAiChatModel openAiChatModel;

    @Value("${openai.api-key}")
    private String openaiApiKey;

    private AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


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
                        postDesignDto.setFontName(font.getName());
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

    public String savePostImage(Integer postId, MultipartFile image){
        Post post = postRepository.findById(postId).get();
        post.setImageUrl(uploadImage(postId, image));
        postRepository.save(post);
        PostDto dto = new PostDto();
        return uploadImage(postId, image);
    }

    public String uploadImage(Integer postId, MultipartFile file) {
        try {
            String fileName = "test/" + file.getOriginalFilename();  // 폴더 경로 포함
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // S3에 파일을 업로드
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

            return amazonS3Client.getUrl(bucket, fileName).toString(); // 업로드된 파일의 URL
        } catch (IOException e) {
            return e.toString();
        }
    }

    public String generateImageWithText(Integer postId) {
        try {
            Post post = postRepository.findById(postId).get();
            Integer userId = post.getSenderId();
            Design design =  designRepository.findByUserIdAndYear(userId, nextYear).get();
            Image image = imageRepository.findById(design.getBackgroundId()).get();

            // 1. 배경 이미지 로드
            BufferedImage backgroundImage;
                    //= ImageIO.read(new File(image.getPic()));
            try {
                URL imageUrl = new URL(image.getPic());
                System.out.println("Loading image from URL: " + image.getPic());
                backgroundImage = ImageIO.read(imageUrl);

                if (backgroundImage == null) {
                    throw new RuntimeException("Failed to read image from URL: " + image.getPic());
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error occurred while loading background image from URL: " + image.getPic(), e);
            }

            // 2. 새로운 이미지를 생성 (배경 크기와 동일하게)
            BufferedImage newImage = new BufferedImage(
                    backgroundImage.getWidth(),
                    backgroundImage.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );

            // 3. Graphics2D를 이용하여 텍스트 추가
            Graphics2D graphics = newImage.createGraphics();

            // 배경 이미지 그리기
            graphics.drawImage(backgroundImage, 0, 0, null);

            // 텍스트 스타일 설정
            graphics.setColor(Color.WHITE); // 텍스트 색상
            graphics.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 48)); // 폰트 및 크기

            // 텍스트 위치 설정 (이미지 중앙에 텍스트 배치)
            FontMetrics fontMetrics = graphics.getFontMetrics();
            int x = (backgroundImage.getWidth() - fontMetrics.stringWidth(post.getContents())) / 2;
            int y = backgroundImage.getHeight() / 2;

            // 텍스트 그리기
            graphics.drawString(post.getContents(), x, y);

            // Graphics 종료
            graphics.dispose();

            // 4. 새로운 이미지 저장
            String path = uploadBufferedImageToS3(postId, newImage);

            return path;
        } catch (io.jsonwebtoken.io.IOException e) {
            e.printStackTrace();
            return "이미지 생성 중 오류가 발생했습니다.";
        }
    }

    public String uploadBufferedImageToS3(Integer postId, BufferedImage image) {
        try {
            // BufferedImage를 ByteArrayOutputStream으로 변환
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            byte[] imageBytes = os.toByteArray();

            // S3 업로드를 위한 InputStream 생성
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);

            // 메타데이터 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/png");
            metadata.setContentLength(imageBytes.length);

            // S3 파일 경로 설정
            String fileName = "generated/" + postId + "_" + System.currentTimeMillis() + ".png";

            // S3에 파일 업로드
            amazonS3Client.putObject(bucket, fileName, inputStream, metadata);

            // S3 URL 반환
            System.out.println(amazonS3Client.getUrl(bucket, fileName).toString());
            return amazonS3Client.getUrl(bucket, fileName).toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "S3 업로드 중 오류가 발생했습니다.";
        }
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
