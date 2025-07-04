package com.dao.momentum.retention.prospect.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "월별 근속 지수 통계 DTO")
public class RetentionMonthlyStatDto {

    @Schema(description = "월", example = "3")
    private int month;

    @Schema(description = "평균 근속 지수", example = "75.2")
    private double averageScore;

    @Schema(description = "표준편차", example = "10.4")
    private double stdDeviation;
}
