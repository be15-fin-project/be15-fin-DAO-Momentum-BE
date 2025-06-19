package com.dao.momentum.evaluation.query.service;

import com.dao.momentum.evaluation.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiStatisticsResponseDto;

public interface KpiStatisticsService {

    KpiStatisticsResponseDto getStatistics(KpiStatisticsRequestDto requestDto);

}
