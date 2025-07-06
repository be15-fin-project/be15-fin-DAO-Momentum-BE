package com.dao.momentum.retention.prospect.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "부서별 안정성 비율 원시 DTO")
public record StabilityRatioByDeptRaw(
        @Schema(description = "부서명", example = "기획팀")
        String deptName,

        @Schema(description = "직위명", example = "대리")
        String positionName,

        @Schema(description = "안정형 인원 수", example = "15")
        Long stableCount,

        @Schema(description = "경고형 인원 수", example = "5")
        Long warningCount,

        @Schema(description = "불안정형 인원 수", example = "3")
        Long unstableCount,

        @Schema(description = "총 인원 수", example = "23")
        Long totalCount
) {
}
