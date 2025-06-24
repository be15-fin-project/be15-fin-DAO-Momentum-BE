package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.evaluation.kpi.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiTimeseriesRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiTimeseriesResponseDto;

public interface KpiStatisticsService {

    // KPI 종합 현황 통계 조회
    KpiStatisticsResponseDto getStatistics(KpiStatisticsRequestDto requestDto);

    // KPI 시계열 통계 조회
    KpiTimeseriesResponseDto getTimeseriesStatistics(KpiTimeseriesRequestDto requestDto);

    // 권한 반영된 KPI 통계 조회
    KpiStatisticsResponseDto getStatisticsWithAccessControl(KpiStatisticsRequestDto requestDto, Long requesterEmpId);

    // 권한 반영된 KPI 시계열 통계 조회
    KpiTimeseriesResponseDto getTimeseriesWithAccessControl(KpiTimeseriesRequestDto requestDto, Long requesterEmpId);
}