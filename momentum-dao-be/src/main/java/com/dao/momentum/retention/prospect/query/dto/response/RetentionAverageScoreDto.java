package com.dao.momentum.retention.prospect.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "근속 전망 평균 점수 및 안정성 요약 DTO")
public class RetentionAverageScoreDto {

    @Schema(description = "평균 점수", example = "72.6")
    private Double averageScore;

    @Schema(description = "총 사원 수", example = "123")
    private Integer totalEmpCount;

    @Schema(description = "안정형 비율 (%)", example = "46.7")
    private Double stabilitySafeRatio;

    @Schema(description = "위험군 비율 (%)", example = "15.2")
    private Double stabilityRiskRatio;
}
