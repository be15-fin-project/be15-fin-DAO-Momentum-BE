package com.dao.momentum.evaluation.kpi.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "KPI 엑셀 자료 요청 DTO")
public record KpiExelRequestDto(

        @Schema(description = "사번", example = "20240001")
        String empNo,

        @Schema(description = "부서 ID", example = "10")
        Integer deptId,

        @Schema(description = "직위 ID", example = "5")
        Integer positionId,

        @Schema(
                description = "KPI 상태 ID",
                example = "2",
                allowableValues = {"1", "2", "3", "4", "5"}
        )
        Integer statusId,

        @Schema(description = "작성일 시작일자 (yyyy-MM-dd)", example = "2025-06-01")
        String startDate,

        @Schema(description = "작성일 종료일자 (yyyy-MM-dd)", example = "2025-06-30")
        String endDate

) {}
