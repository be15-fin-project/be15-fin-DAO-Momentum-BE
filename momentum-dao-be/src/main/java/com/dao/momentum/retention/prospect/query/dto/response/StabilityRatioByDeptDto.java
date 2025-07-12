package com.dao.momentum.retention.prospect.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "근속 안정성 유형 분포 DTO")
public record StabilityRatioByDeptDto(
        @Schema(description = "부서 ID", example = "인사팀")
        Integer deptId,

        @Schema(description = "부서 이름", example = "인사팀")
        String deptName,

        @Schema(description = "직급 ID", example = "사원")
        Integer positionId,

        @Schema(description = "직급 이름", example = "사원")
        String positionName,

        @Schema(description = "전체 사원 수", example = "100")
        int empCount,

        @Schema(description = "20% 사원 비중", example = "10")
        int progress20,

        @Schema(description = "40% 사원 비중", example = "20")
        int progress40,

        @Schema(description = "60% 사원 비중", example = "30")
        int progress60,

        @Schema(description = "80% 사원 비중", example = "25")
        int progress80,

        @Schema(description = "100% 사원 비중", example = "15")
        int progress100
) {
}
