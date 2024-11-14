package com.aendyear.komawatsir.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiverDto {
    private Integer id;

    @NotNull
    private Integer senderId;

    private Integer receiverUserId;

    private String nickname;

    @Size(min = 10, max = 11)
    private String tel;

    private String memo;

    private String year;

    private Boolean isDeleted;
}
