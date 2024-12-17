package com.aendyear.komawatsir.dto;

import com.aendyear.komawatsir.type.FontColor;
import com.aendyear.komawatsir.type.FontSize;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostImageDto {
    private int postId;

    private String contents;

    private String imageUrl;

    private String font;

    private FontColor fontColor;

    private FontSize fontSize;
}
