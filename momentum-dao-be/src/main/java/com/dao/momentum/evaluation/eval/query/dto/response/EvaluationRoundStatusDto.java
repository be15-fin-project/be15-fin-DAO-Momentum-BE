package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "오늘 진행 중인 평가 회차 정보")
@Builder
public record EvaluationRoundStatusDto(

        @Schema(description = "진행 중인 회차가 있는지 여부", example = "true")
        boolean inProgress,

        @Schema(description = "진행 중인 평가 회차 ID (없으면 null)", example = "5")
        Long roundId
) { }
