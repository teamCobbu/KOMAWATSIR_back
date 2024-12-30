package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.PostDesignDto;
import com.aendyear.komawatsir.dto.PostDto;
import com.aendyear.komawatsir.dto.PostImageDto;
import com.aendyear.komawatsir.dto.PresentDto;
import com.aendyear.komawatsir.entity.Font;
import com.aendyear.komawatsir.entity.Image;
import com.aendyear.komawatsir.entity.*;
import com.aendyear.komawatsir.repository.*;
import com.aendyear.komawatsir.type.FontColor;
import com.aendyear.komawatsir.type.FontSize;
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
import java.io.*;
import java.net.HttpURLConnection;
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

    @Autowired
    private AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;


    // 연도별 받은 연하장
    public List<PresentDto> getShowCard(Integer receiverUserId, String year) {
        List<PresentDto> result = new ArrayList<>();

        receiverRepository.findByReceiverUserIdAndYear(receiverUserId, year).forEach(receiver -> {
            List<Post> posts = postRepository.findByReceiverIdAndYearAndStatusNot(receiver.getId(), year, PostStatus.DELETED);
            System.out.println(posts.size());
            posts.forEach(post -> {
                PresentDto present = Mapper.toPresentDto(post);

                Optional<Design> designs = designRepository.findByUserIdAndYear(post.getSenderId(), post.getYear());
                if (designs.isPresent()) { // thumbnail
                    Design design = designs.get();
                    Optional<Image> thumbnail = imageRepository.findById(design.getThumbnailId());
                    thumbnail.ifPresent(image -> present.setFront(image.getPic()));
                }
                result.add(present);
            });
        });
        return result;
    }

    // 유저가 받은 전체 연하장
    public List<PresentDto> getCardsByUser(Integer receiverUserId) {
        List<PresentDto> result = new ArrayList<>();
        receiverRepository.findByReceiverUserId(receiverUserId).forEach(receiver -> {
            List<Post> posts = postRepository.findByReceiverIdAndStatusNot(receiver.getId(), PostStatus.DELETED);
            posts.forEach(post -> {
                PresentDto present = Mapper.toPresentDto(post);

                Optional<Design> designs = designRepository.findByUserIdAndYear(post.getSenderId(), post.getYear());
                if (designs.isPresent()) {
                    Design design = designs.get(); // thumbnail
                    Optional<Image> thumbnail = imageRepository.findById(design.getThumbnailId());
                    thumbnail.ifPresent(image -> present.setFront(image.getPic()));
                }
                result.add(present);
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
            post.setImageUrl(saveAsImage(post)); // 이미지를 만들고 image_url 필드에 저장
        }

        return postRepository.save(post);
    }

    // 로컬 폰트 로드
    public java.awt.Font loadLocalFont(String fontPath, float size) {
        try (InputStream fontStream = getClass().getClassLoader().getResourceAsStream(fontPath)) {
            if (fontStream == null) {
                throw new RuntimeException("폰트 파일을 찾을 수 없습니다: " + fontPath);
            }
            return java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, fontStream).deriveFont(size);
        } catch (Exception e) {
            throw new RuntimeException("폰트 파일 로드 중 오류 발생", e);
        }
    }

    public String saveAsImage(Post post) {
        PostDesignDto design = getPostDesign(post.getSenderId());
        PostImageDto dto = PostImageDto.builder()
                .postId(post.getId())
                .contents(post.getContents())
                .imageUrl(design.getBackgroundPic())
                .font(design.getFontName())
                .fontColor(design.getFontColor())
                .fontSize(design.getFontSize())
                .build();
        try {
            // 1. 배경 이미지 로드
            BufferedImage backgroundImage;
            try {
                URL imageUrl = new URL(dto.getImageUrl());
                backgroundImage = ImageIO.read(imageUrl);
                if (backgroundImage == null) {
                    throw new RuntimeException("Failed to read image from URL: ");
                }
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("Error occurred while loading background image from URL: ", e);
            }

            // 2. 새로운 이미지 생성 (배경 크기와 동일하게)
            BufferedImage newImage = new BufferedImage(
                    backgroundImage.getWidth(),
                    backgroundImage.getHeight(),
                    BufferedImage.TYPE_INT_ARGB
            );
            Graphics2D graphics = newImage.createGraphics();
            graphics.drawImage(backgroundImage, 0, 0, null);

            // 3. 로컬 폰트 파일 로드
            java.awt.Font customFont = loadLocalFont("fonts/" + dto.getFont() + ".ttf", dto.getFontSize().equals(FontSize.defaultSize) ? 100f : 150f).deriveFont(java.awt.Font.BOLD);
            graphics.setFont(customFont);

            // 4. 텍스트 스타일 및 색상 설정
            graphics.setColor(dto.getFontColor().equals(FontColor.black) ? Color.BLACK : Color.WHITE);

            // 5. 텍스트 위치를 이미지 가운데에 정렬
            String text = dto.getContents();
            int maxWidth = backgroundImage.getWidth() - 100; // 최대 너비 여백 고려
            int centerX = backgroundImage.getWidth() / 2;    // 이미지 가로 중앙
            int startY = backgroundImage.getHeight() / 2 - 50; // 시작 Y 좌표 (위에서부터 적절히 조정)
            drawMultilineTextCentered(graphics, text, centerX, startY, maxWidth);

            // 6. 이미지 저장
            graphics.dispose();
            String path = uploadBufferedImgToS3(newImage);
            return path;
        } catch (Exception e) {
            e.printStackTrace();
            return "이미지 생성 중 오류가 발생했습니다.";
        }
    }

    public String uploadBufferedImgToS3(BufferedImage image) {
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
            String fileName = "komawotsir-" + System.currentTimeMillis() + ".png";

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

    public void drawMultilineTextCentered(Graphics2D graphics, String text, int centerX, int startY, int maxWidth) {
        FontMetrics fontMetrics = graphics.getFontMetrics();
        int lineHeight = fontMetrics.getHeight();

        // 텍스트를 나누기 위한 StringBuilder
        StringBuilder line = new StringBuilder();
        int currentY = startY;

        for (String word : text.split(" ")) {
            if (fontMetrics.stringWidth(line + word) > maxWidth) {
                // 현재 줄이 maxWidth를 초과하면 출력
                int lineWidth = fontMetrics.stringWidth(line.toString());
                int x = centerX - (lineWidth / 2); // 가로 중앙 정렬
                graphics.drawString(line.toString(), x, currentY);

                // 다음 줄로 이동
                line = new StringBuilder(word).append(" ");
                currentY += lineHeight;
            } else {
                line.append(word).append(" ");
            }
        }

        // 남은 텍스트 출력 (마지막 줄)
        if (!line.isEmpty()) {
            int lineWidth = fontMetrics.stringWidth(line.toString());
            int x = centerX - (lineWidth / 2);
            graphics.drawString(line.toString(), x, currentY);
        }
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

        Optional<Design> design = designRepository.findByUserIdAndYear(userId, nextYear);
        if (design.isPresent()) {
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
}
