package com.dao.momentum.retention.prospect.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "근속 지수 시계열 요청 DTO")
public record RetentionTimeseriesRequestDto(
        @Schema(description = "연도", example = "2024")
        Integer year,

        @Schema(description = "부서 ID", example = "12")
        Integer deptId,

        @Schema(description = "직급 ID", example = "3")
        Integer positionId
) {
}
