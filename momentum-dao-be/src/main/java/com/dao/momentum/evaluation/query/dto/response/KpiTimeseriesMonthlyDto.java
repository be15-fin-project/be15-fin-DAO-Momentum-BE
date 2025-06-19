package com.dao.momentum.evaluation.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KpiTimeseriesMonthlyDto {
    private int month;              // 월 (1~12)
    private int totalKpiCount;      // 해당 월 KPI 작성 수
    private int completedKpiCount;  // 완료된 KPI 수
    private double averageProgress; // 평균 진척률 (%)
}
