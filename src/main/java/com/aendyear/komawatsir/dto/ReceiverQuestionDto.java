package com.aendyear.komawatsir.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiverQuestionDto {

    private Integer id;

    @NotNull
    private Integer inquiryItemId;

    @NotNull
    private Integer receiverId;

    @Size(min = 1, max = 100)
    private String answer;

    private String question;
}
