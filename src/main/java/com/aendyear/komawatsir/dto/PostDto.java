package com.aendyear.komawatsir.dto;

import com.aendyear.komawatsir.entity.Post;
import com.aendyear.komawatsir.type.PostStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostDto {

    private Integer id;

    @NotNull
    private Integer senderId;


    private String senderNickname;

    private Integer receiverId;

    @NotNull
    private String contents;

    private PostStatus status;

    private String year;
}