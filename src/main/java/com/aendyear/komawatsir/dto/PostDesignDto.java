package com.aendyear.komawatsir.dto;

import com.aendyear.komawatsir.type.FontColor;
import com.aendyear.komawatsir.type.FontSize;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDesignDto {

    // 기본 정보
    private Integer postId, senderId, receiverId, designId;
    private String senderNickname, contents, year;

    // design (썸네일, 배경)
    private Integer thumbnailId, backgroundId;
    private String thumbnailPic, backgroundPic;

    // font (폰트 크기, url, 색)
    private Integer fontId;
    private FontSize fontSize;
    private FontColor fontColor;
    private String fontUrl;
    private String fontName;
}
