package com.dao.momentum.evaluation.kpi.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KpiStatisticsRequestDto {

    @Schema(description = "조회 연도", example = "2025")
    private Integer year;

    @Schema(description = "조회 월", example = "6")
    private Integer month;

    @Schema(description = "사번", example = "1001")
    private String empNo;

    @Schema(description = "부서 ID", example = "10")
    private Long deptId;

    @Schema(description = "직위 ID", example = "5")
    private Integer positionId;
}
