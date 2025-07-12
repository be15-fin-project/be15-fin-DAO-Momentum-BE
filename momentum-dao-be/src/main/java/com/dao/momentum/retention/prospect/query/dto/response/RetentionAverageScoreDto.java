package com.dao.momentum.retention.prospect.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "평균 근속 지수 및 전체 통계 DTO")
public record RetentionAverageScoreDto(

        @Schema(description = "평균 근속 지수", example = "78.5")
        Double averageScore,

        @Schema(description = "전체 사원 수", example = "124")
        Long totalEmpCount,

        @Schema(description = "안정군 비율 (%)", example = "82.1")
        Double stabilitySafeRatio,

        @Schema(description = "위험군 비율 (%)", example = "17.9")
        Double stabilityRiskRatio
) {}
