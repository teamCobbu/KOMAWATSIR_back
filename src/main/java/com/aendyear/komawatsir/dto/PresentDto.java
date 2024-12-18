// 연하장 이미지 생성 시 사용
package com.aendyear.komawatsir.dto;

import com.aendyear.komawatsir.type.FontColor;
import com.aendyear.komawatsir.type.FontSize;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PresentDto {
    private int postId;

    private String sender;

    private String front;

    private String back;
}
