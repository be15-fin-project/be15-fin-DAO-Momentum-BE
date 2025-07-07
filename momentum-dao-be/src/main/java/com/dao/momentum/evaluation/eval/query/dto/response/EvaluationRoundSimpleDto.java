package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "평가 회차 요약 DTO")
@Builder
public record EvaluationRoundSimpleDto(

        @Schema(description = "평가 회차 ID", example = "1")
        Long roundId,

        @Schema(description = "평가 회차 번호 (ex: 2024-1차)", example = "2024-1차")
        String roundNo
) { }
