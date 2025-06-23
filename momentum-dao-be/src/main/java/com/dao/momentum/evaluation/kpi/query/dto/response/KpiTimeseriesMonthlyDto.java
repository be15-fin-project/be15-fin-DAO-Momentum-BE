package com.dao.momentum.evaluation.kpi.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "월별 KPI 시계열 데이터 DTO")
public class KpiTimeseriesMonthlyDto {

    @Schema(description = "월 (1~12)", example = "6")
    private int month;

    @Schema(description = "해당 월 KPI 작성 수", example = "12")
    private int totalKpiCount;

    @Schema(description = "완료된 KPI 수", example = "8")
    private int completedKpiCount;

    @Schema(description = "평균 진척률 (%)", example = "66.7")
    private double averageProgress;
}
