package com.dao.momentum.evaluation.kpi.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "월별 KPI 시계열 데이터 DTO")
public record KpiTimeseriesMonthlyDto(

        @Schema(description = "월 (1~12)", example = "6")
        int month,

        @Schema(description = "해당 월 KPI 작성 수", example = "12")
        int totalKpiCount,

        @Schema(description = "완료된 KPI 수", example = "8")
        int completedKpiCount,

        @Schema(description = "평균 진척률 (%)", example = "66.7")
        double averageProgress

) {}
