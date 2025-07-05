package com.dao.momentum.retention.prospect.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "근속 전망 평균 점수 및 안정성 요약 DTO")
public record RetentionAverageScoreDto(
        @Schema(description = "평균 점수", example = "72.6")
        Double averageScore,

        @Schema(description = "총 사원 수", example = "123")
        Integer totalEmpCount,

        @Schema(description = "안정형 비율 (%)", example = "46.7")
        Double stabilitySafeRatio,

        @Schema(description = "위험군 비율 (%)", example = "15.2")
        Double stabilityRiskRatio
) {
}
