package com.dao.momentum.evaluation.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Schema(description = "KPI 통계 응답 DTO")
public class KpiStatisticsResponseDto {

    @Schema(description = "전체 KPI 수", example = "35")
    private int totalKpiCount;

    @Schema(description = "완료된 KPI 수", example = "21")
    private int completedKpiCount;

    @Schema(description = "평균 진척도 (%)", example = "62.5")
    private double averageProgress;
}
