package com.dao.momentum.evaluation.kpi.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "대시보드 KPI 조회 요청 DTO")
public record KpiDashboardRequestDto(

        @Schema(description = "조회 시작일 (yyyy-MM-dd)", example = "2025-07-01")
        String startDate,

        @Schema(description = "조회 종료일 (yyyy-MM-dd)", example = "2025-07-10")
        String endDate,

        @Schema(description = "조회 최대 개수 (선택)", example = "3")
        Integer limit

) {}
