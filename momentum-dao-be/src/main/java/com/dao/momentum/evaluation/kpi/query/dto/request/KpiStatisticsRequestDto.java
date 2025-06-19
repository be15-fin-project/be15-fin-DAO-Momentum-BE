package com.dao.momentum.evaluation.kpi.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class KpiStatisticsRequestDto {

    @Schema(description = "조회 연도", example = "2025")
    private Integer year;

    @Schema(description = "조회 월", example = "6")
    private Integer month;

    @Schema(description = "부서 ID", example = "10")
    private Long deptId;

    @Schema(description = "사원 ID", example = "1001")
    private Long empId;

    @Schema(description = "사용자 권한 (예: MASTER, HR_MANAGER, BOOKKEEPING, MANAGER)", example = "MASTER")
    private String userRole;
}
