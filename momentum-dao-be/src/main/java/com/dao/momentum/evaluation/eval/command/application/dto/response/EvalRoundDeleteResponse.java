package com.dao.momentum.evaluation.eval.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "다면 평가 회차 삭제 응답 DTO")
public class EvalRoundDeleteResponse {

    @Schema(description = "삭제된 회차 ID", example = "12")
    private final Integer roundId;

    @Schema(description = "삭제 성공 메시지", example = "평가 회차가 성공적으로 삭제되었습니다.")
    private final String message;
}
