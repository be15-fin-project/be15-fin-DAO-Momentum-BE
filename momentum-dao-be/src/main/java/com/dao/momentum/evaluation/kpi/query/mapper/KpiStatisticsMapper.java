package com.dao.momentum.evaluation.kpi.query.mapper;

import com.dao.momentum.evaluation.kpi.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiTimeseriesRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiTimeseriesMonthlyDto;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KpiStatisticsMapper {

    // KPI 종합 현황 통계 조회
    KpiStatisticsResponseDto getMonthlyStatistics(KpiStatisticsRequestDto dto);

    // KPI 시계열 통계 조회
    List<KpiTimeseriesMonthlyDto> getTimeseriesStatistics(KpiTimeseriesRequestDto dto);
}
