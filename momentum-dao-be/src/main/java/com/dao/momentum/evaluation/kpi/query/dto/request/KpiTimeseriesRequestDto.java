package com.dao.momentum.evaluation.kpi.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "KPI 시계열 조회 요청 DTO")
public record KpiTimeseriesRequestDto(

        @Schema(
                description = "조회 연도 (nullable, 미입력 시 현재 연도 사용)",
                example = "2025"
        )
        Integer year,

        @Schema(description = "사원 번호", example = "1001")
        String empNo,

        @Schema(description = "부서 ID", example = "10")
        Long deptId,

        @Schema(description = "직위 ID", example = "5")
        Integer positionId

) {}
