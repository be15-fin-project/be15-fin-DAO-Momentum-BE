package com.dao.momentum.evaluation.kpi.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "대시보드 KPI 응답 DTO")
public record KpiDashboardResponseDto(

        @Schema(description = "KPI 식별자", example = "101")
        Long kpiId,

        @Schema(description = "KPI 목표 내용", example = "월간 영업 건수 10건 달성")
        String goal,

        @Schema(description = "KPI 목표 수치", example = "10")
        int goalValue,

        @Schema(description = "KPI 진척도 (%)", example = "80")
        int kpiProgress,

        @Schema(description = "KPI 상태 타입", example = "ACCEPTED")
        String statusType,

        @Schema(description = "KPI 등록일", example = "2025-07-01")
        String createdAt,

        @Schema(description = "KPI 마감일", example = "2025-07-31")
        String deadline

) {}
