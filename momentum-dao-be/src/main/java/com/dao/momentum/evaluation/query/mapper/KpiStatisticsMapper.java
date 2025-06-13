package com.dao.momentum.evaluation.query.mapper;

import com.dao.momentum.evaluation.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiStatisticsResponseDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface KpiStatisticsMapper {

    KpiStatisticsResponseDto getMonthlyStatistics(KpiStatisticsRequestDto dto);

}
