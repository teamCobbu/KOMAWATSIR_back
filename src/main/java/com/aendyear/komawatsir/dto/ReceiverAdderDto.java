// 수신 신청 시 수신자와 답변을 하나의 트랜잭션 내에서 처리하기 위해 사용되는 DTO
package com.aendyear.komawatsir.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReceiverAdderDto {
    private ReceiverDto receiver;
    private List<ReceiverQuestionDto> answers;
}
