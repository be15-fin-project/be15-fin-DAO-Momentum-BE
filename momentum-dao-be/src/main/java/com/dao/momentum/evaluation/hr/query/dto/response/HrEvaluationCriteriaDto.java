package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "인사 평가 기준 조회 응답 DTO")
public record HrEvaluationCriteriaDto(
        @Schema(description = "등급 비율 정보")
        RateInfo rateInfo,

        @Schema(description = "요인별 가중치 정보")
        WeightInfo weightInfo
) {
}
