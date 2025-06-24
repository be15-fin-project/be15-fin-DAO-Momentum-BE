package com.dao.momentum.retention.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "부서별 근속 안정성 유형 분포 DTO")
public class StabilityDistributionByDeptDto {

    @Schema(description = "부서명", example = "인사팀")
    private final String deptName;

    @Schema(description = "안정형 비율 (%)", example = "55.0")
    private final double stableRatio;

    @Schema(description = "주의형 비율 (%)", example = "30.0")
    private final double warningRatio;

    @Schema(description = "불안정형 비율 (%)", example = "15.0")
    private final double unstableRatio;
}
