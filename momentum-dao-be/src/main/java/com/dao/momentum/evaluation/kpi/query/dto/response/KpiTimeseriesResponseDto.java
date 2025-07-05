package com.dao.momentum.evaluation.kpi.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "KPI 시계열 응답 DTO (연도별 월간 통계 포함)")
public record KpiTimeseriesResponseDto(

        @Schema(description = "조회 기준 연도", example = "2025")
        int year,

        @Schema(description = "월별 KPI 통계 리스트")
        List<KpiTimeseriesMonthlyDto> monthlyStats

) {}
