package com.dao.momentum.evaluation.query.dto.response;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
public class KpiStatisticsResponseDto {
    private int totalKpiCount;
    private int completedKpiCount;
    private double averageProgress;
}
