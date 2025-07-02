package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "오늘 진행 중인 평가 회차 정보")
public class EvaluationRoundStatusDto {
    @Schema(description = "진행 중인 회차가 있는지 여부", example = "true")
    private boolean inProgress;

    @Schema(description = "진행 중인 평가 회차 ID (없으면 null)", example = "5")
    private Long roundId;
}
