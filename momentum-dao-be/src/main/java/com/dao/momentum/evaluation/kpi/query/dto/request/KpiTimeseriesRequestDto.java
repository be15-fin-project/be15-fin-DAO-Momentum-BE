package com.dao.momentum.evaluation.kpi.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "KPI 시계열 조회 요청 DTO")
public class KpiTimeseriesRequestDto {

    @Schema(
            description = "조회 연도 (nullable, 미입력 시 현재 연도 사용)",
            example = "2025"
    )
    private Integer year;

    @Schema(description = "사원 번호", example = "1001")
    private String empNo;

    @Schema(description = "부서 ID", example = "10")
    private Long deptId;

    @Schema(description = "직위 ID", example = "5")
    private Integer positionId;
}
