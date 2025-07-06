package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "인사 평가 상세 응답 DTO")
public record HrEvaluationDetailResultDto(
        @Schema(description = "인사 평가 기본 정보")
        HrEvaluationDetailDto content,

        @Schema(description = "등급 비율 정보")
        RateInfo rateInfo,

        @Schema(description = "요인별 가중치 정보")
        WeightInfo weightInfo,

        @Schema(description = "요인별 점수 목록")
        List<FactorScoreDto> factorScores
) {
}
