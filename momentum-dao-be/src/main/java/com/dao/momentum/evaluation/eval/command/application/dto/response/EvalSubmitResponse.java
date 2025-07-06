package com.dao.momentum.evaluation.eval.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "다면 평가 제출 응답 DTO")
@Builder
public record EvalSubmitResponse(

        @Schema(description = "평가 결과 ID", example = "10001")
        Long resultId,

        @Schema(description = "제출 완료 메시지", example = "평가가 성공적으로 제출되었습니다.")
        String message
) { }
