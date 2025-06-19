package com.dao.momentum.evaluation.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.exception.KpiException;
import com.dao.momentum.evaluation.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.query.dto.response.KpiTimeseriesMonthlyDto;
import com.dao.momentum.evaluation.query.dto.response.KpiTimeseriesResponseDto;
import com.dao.momentum.evaluation.query.mapper.KpiStatisticsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

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

    @Override
    public KpiTimeseriesResponseDto getTimeseriesStatistics(Integer year) {
        int targetYear = (year != null) ? year : LocalDate.now().getYear();
        List<KpiTimeseriesMonthlyDto> stats = kpiStatisticsMapper.getTimeseriesStatistics(targetYear);


        if (stats == null || stats.isEmpty()) {
            throw new KpiException(ErrorCode.STATISTICS_NOT_FOUND);
        }

        return new KpiTimeseriesResponseDto(targetYear, stats);
    }
}
