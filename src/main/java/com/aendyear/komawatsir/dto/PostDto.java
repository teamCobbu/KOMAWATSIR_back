package com.aendyear.komawatsir.dto;

import com.aendyear.komawatsir.entity.Post;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {
    private Integer id;

    private Integer senderId;

    private String senderNickname;

    private Integer receiverId;

    private String contents;

    private Post.Status status;

    private Integer year;

    public enum Status {
        PENDING, PROGRESSING, COMPLETED, DELETED
    }
}
