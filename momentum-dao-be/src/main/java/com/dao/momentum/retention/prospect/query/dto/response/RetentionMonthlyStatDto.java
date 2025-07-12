package com.dao.momentum.retention.prospect.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "월별 근속 지수 통계 DTO")
public record RetentionMonthlyStatDto(

        @Schema(description = "연도", example = "2025")
        int year,

        @Schema(description = "월", example = "6")
        int month,

        @Schema(description = "해당 월 평균 근속 지수", example = "76.4")
        double averageScore,

        @Schema(description = "표준 편차", example = "8.32")
        double stdDeviation
) {}
