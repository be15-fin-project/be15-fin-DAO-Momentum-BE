package com.dao.momentum.evaluation.kpi.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "KPI 통계 응답 DTO")
public record KpiStatisticsResponseDto(

        @Schema(description = "전체 KPI 수", example = "35")
        int totalKpiCount,

        @Schema(description = "완료된 KPI 수", example = "21")
        int completedKpiCount,

        @Schema(description = "평균 진척도 (%)", example = "62.5")
        double averageProgress

) {}
