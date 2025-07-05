package com.dao.momentum.retention.prospect.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "월별 근속 지수 통계 DTO")
public record RetentionMonthlyStatDto(
        @Schema(description = "월", example = "3")
        int month,

        @Schema(description = "평균 근속 지수", example = "75.2")
        double averageScore,

        @Schema(description = "표준편차", example = "10.4")
        double stdDeviation
) {
}
