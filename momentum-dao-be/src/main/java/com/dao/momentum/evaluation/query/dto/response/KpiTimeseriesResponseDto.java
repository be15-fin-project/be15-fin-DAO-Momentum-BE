package com.dao.momentum.evaluation.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KpiTimeseriesResponseDto {
    private int year;                                      // 조회 기준 연도
    private List<KpiTimeseriesMonthlyDto> monthlyStats;    // 월별 통계 리스트
}
