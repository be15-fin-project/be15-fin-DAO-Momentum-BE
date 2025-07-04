package com.dao.momentum.retention.prospect.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "근속 안정성 유형 분포 DTO")
public class StabilityDistributionByDeptDto {

    @Schema(description = "부서 ID", example = "인사팀")
    private final Integer deptId;

    @Schema(description = "부서 이름", example = "인사팀")
    private final String deptName;

    @Schema(description = "직급 ID", example = "사원")
    private final Integer positionId;

    @Schema(description = "직급 이름", example = "사원")
    private final String positionName;

    @Schema(description = "전체 사원 수", example = "100")
    private final int empCount;

    @Schema(description = "20% 사원 비중", example = "10")
    private final int progress20;

    @Schema(description = "40% 사원 비중", example = "20")
    private final int progress40;

    @Schema(description = "60% 사원 비중", example = "30")
    private final int progress60;

    @Schema(description = "80% 사원 비중", example = "25")
    private final int progress80;

    @Schema(description = "100% 사원 비중", example = "15")
    private final int progress100;
}
