package com.aendyear.komawatsir.service;

import com.aendyear.komawatsir.dto.ImageDto;
import com.aendyear.komawatsir.entity.Image;
import com.aendyear.komawatsir.repository.ImageRepository;
import com.aendyear.komawatsir.type.ImageCategory;
import com.aendyear.komawatsir.type.SourceType;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Autowired
    private AmazonS3Client amazonS3Client;

    // 이미지 정보 조회
    public ImageDto getSingleImage(Integer imageId) {
        ImageDto result = new ImageDto();

        Optional<Image> image = imageRepository.findById(imageId);
        if (image.isPresent()) {
            result = Mapper.toDto(image.get());
        }

        return result;
    }

    public List<ImageDto> getAllImage(ImageCategory category, Integer userId, boolean isFront) {

        return imageRepository.findByCategoryAndEtc(category, SourceType.SERVICE, isFront, userId)
                .stream()
                .map(Mapper::toDto)
                .toList();
    }

    public String analyzeImage(String imageKey) {
        try {
            // URL에서 객체 키 추출
            if (imageKey.startsWith("http")) {
                imageKey = extractObjectKey(imageKey);
            }

            // S3에서 객체 가져오기
            S3Object s3Object = amazonS3Client.getObject(new GetObjectRequest(bucketName, imageKey));
            InputStream objectData = s3Object.getObjectContent();

            // 이미지를 BufferedImage로 변환
            BufferedImage image = ImageIO.read(objectData);
            if (image == null) {
                throw new RuntimeException("이미지를 로드할 수 없습니다.");
            }

            // 밝기 계산
            long totalBrightness = 0;
            int width = image.getWidth();
            int height = image.getHeight();
            int totalPixels = width * height;

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int rgb = image.getRGB(x, y);

                    int red = (rgb >> 16) & 0xFF;
                    int green = (rgb >> 8) & 0xFF;
                    int blue = rgb & 0xFF;

                    // 밝기 계산 (가중 평균 방식)
                    double brightness = 0.299 * red + 0.587 * green + 0.114 * blue;
                    totalBrightness += (long) brightness;
                }
            }

            double averageBrightness = (double) totalBrightness / totalPixels;
            return (averageBrightness > 127.5) ? "bright" : "dark";

        } catch (Exception e) {
            throw new RuntimeException("analyzeImage ERROR : " + e.getMessage(), e);
        }
    }

    private String extractObjectKey(String url) {
        try {
            URI uri = new URI(url);

            String path = uri.getPath();

            if (path.startsWith("/")) {
                path = path.substring(1);
            }
            return path;
        } catch (Exception e) {
            throw new IllegalArgumentException("extractObjectKey ERROR : " + e.getMessage(), e);
        }
    }
}
