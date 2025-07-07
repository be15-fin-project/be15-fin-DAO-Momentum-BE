package com.dao.momentum.evaluation.kpi.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "KPI 통계 요청 DTO")
public record KpiStatisticsRequestDto(

        @Schema(description = "조회 연도", example = "2025")
        Integer year,

        @Schema(description = "조회 월", example = "6")
        Integer month,

        @Schema(description = "사번", example = "1001")
        String empNo,

        @Schema(description = "부서 ID", example = "10")
        Long deptId,

        @Schema(description = "직위 ID", example = "5")
        Integer positionId

) {}
