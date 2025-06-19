package com.dao.momentum.evaluation.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.exception.KpiException;
import com.dao.momentum.evaluation.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.query.mapper.KpiStatisticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KpiStatisticsServiceImpl implements KpiStatisticsService {

    private final KpiStatisticsMapper kpiStatisticsMapper;

    @Override
    public KpiStatisticsResponseDto getStatistics(KpiStatisticsRequestDto requestDto) {
        KpiStatisticsResponseDto result = kpiStatisticsMapper.getMonthlyStatistics(requestDto);

        if (result == null) {
            throw new KpiException(ErrorCode.UNKNOWN_ERROR);
        }

        return result;
    }
}
