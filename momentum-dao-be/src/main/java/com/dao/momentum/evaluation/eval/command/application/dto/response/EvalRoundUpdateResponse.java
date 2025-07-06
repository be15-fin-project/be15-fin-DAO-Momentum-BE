package com.dao.momentum.evaluation.eval.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "다면 평가 회차 수정 응답 DTO")
@Builder
public record EvalRoundUpdateResponse(

        @Schema(description = "수정된 회차 ID", example = "5")
        Integer roundId,

        @Schema(description = "성공 메시지", example = "평가 회차가 성공적으로 수정되었습니다.")
        String message
) { }
