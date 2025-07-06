package com.dao.momentum.evaluation.eval.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "다면 평가 회차 삭제 응답 DTO")
@Builder
public record EvalRoundDeleteResponse(

        @Schema(description = "삭제된 회차 ID", example = "12")
        Integer roundId,

        @Schema(description = "삭제 성공 메시지", example = "평가 회차가 성공적으로 삭제되었습니다.")
        String message
) { }
