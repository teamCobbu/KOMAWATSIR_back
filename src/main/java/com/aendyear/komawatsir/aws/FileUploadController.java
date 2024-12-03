package com.aendyear.komawatsir.aws;

import com.aendyear.komawatsir.entity.Image;
import com.aendyear.komawatsir.repository.ImageRepository;
import com.aendyear.komawatsir.type.ImageCategory;
import com.aendyear.komawatsir.type.SourceType;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final AmazonS3Client amazonS3Client;

    // S3 버킷 이름을 가져옵니다.
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Autowired
    private ImageRepository imageRepository;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("userId") Integer userId) {
        try {
            String fileName = "test/" + file.getOriginalFilename();  // 폴더 경로 포함
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            // S3에 파일을 업로드
            amazonS3Client.putObject(bucket, fileName, file.getInputStream(), metadata);

            // 업로드된 파일의 URL을 가져옵니다.
            String fileUrl = amazonS3Client.getUrl(bucket, fileName).toString();

            String customNo = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"));


            imageRepository.save(Image.builder()
                    .category(ImageCategory.CUSTOM)
                    .name(userId + "_" + customNo)
                    .pic(fileUrl)
                    .sourceType(SourceType.USER)
                    .userId(userId)
                    .build());

            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
