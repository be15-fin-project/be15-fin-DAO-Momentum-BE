package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SelfEvaluationDetailResultDto {

    @Schema(description = "기본 평가 정보")
    private final SelfEvaluationResponseDto detail;

    @Schema(description = "요인별 점수 목록")
    private final List<FactorScoreDto> factorScores;
}
