package com.aendyear.komawatsir.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiverQuestionDto {
    private Integer id;

    private Integer inquiryItemId;

    private Integer receiverId;

    private String answer;

    private String question;
}
