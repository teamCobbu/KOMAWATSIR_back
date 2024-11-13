package com.aendyear.komawatsir.dto;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiverDto {
    private Integer id;

    private Integer senderId;

    private Integer receiverUserId;

    private String nickname;

    private String tel;

    private String memo;

    private String year;

    private Boolean isDeleted;
}
