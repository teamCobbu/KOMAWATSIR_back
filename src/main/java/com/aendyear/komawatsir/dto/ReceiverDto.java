package com.aendyear.komawatsir.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiverDto {
    private Integer id;

    private Integer userId;

    private String nickname;

    private String tel;

    private String memo;
}
