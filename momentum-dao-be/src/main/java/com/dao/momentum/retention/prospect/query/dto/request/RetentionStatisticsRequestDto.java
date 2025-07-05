package com.dao.momentum.retention.prospect.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "근속 전망 통계 조회 요청 DTO")
public record RetentionStatisticsRequestDto(
        @Schema(description = "회차 ID", example = "3")
        Integer roundId,

        @Schema(description = "부서 ID", example = "10")
        Integer deptId,

        @Schema(description = "직급 ID", example = "5")
        Integer positionId
) {
}
