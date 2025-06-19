package com.dao.momentum.evaluation.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.exception.KpiException;
import com.dao.momentum.evaluation.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.query.dto.response.KpiTimeseriesMonthlyDto;
import com.dao.momentum.evaluation.query.dto.response.KpiTimeseriesResponseDto;
import com.dao.momentum.evaluation.query.mapper.KpiStatisticsMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiStatisticsServiceImplTest {

    @Mock
    private KpiStatisticsMapper kpiStatisticsMapper;

    @InjectMocks
    private KpiStatisticsServiceImpl kpiStatisticsService;

    @Test
    @DisplayName("KPI 통계 정상 조회")
    void getStatistics_success() {
        // given
        KpiStatisticsRequestDto requestDto = new KpiStatisticsRequestDto();
        requestDto.setYear(2025);
        requestDto.setMonth(6);
        requestDto.setDeptId(101L);

        KpiStatisticsResponseDto mockResponse = new KpiStatisticsResponseDto();
        mockResponse.setTotalKpiCount(12);
        mockResponse.setCompletedKpiCount(5);
        mockResponse.setAverageProgress(66.6);

        when(kpiStatisticsMapper.getMonthlyStatistics(requestDto)).thenReturn(mockResponse);

        // when
        KpiStatisticsResponseDto result = kpiStatisticsService.getStatistics(requestDto);

        // then
        assertNotNull(result);
        assertEquals(12, result.getTotalKpiCount());
        assertEquals(5, result.getCompletedKpiCount());
        assertEquals(66.6, result.getAverageProgress());
    }

    @Test
    @DisplayName("KPI 통계 조회 실패 - 결과 없음")
    void getStatistics_nullResponse_throwsException() {
        // given
        KpiStatisticsRequestDto requestDto = new KpiStatisticsRequestDto();
        requestDto.setYear(2025);
        requestDto.setMonth(6);

        when(kpiStatisticsMapper.getMonthlyStatistics(requestDto)).thenReturn(null);

        // when & then
        KpiException ex = assertThrows(KpiException.class, () -> {
            kpiStatisticsService.getStatistics(requestDto);
        });

        assertEquals(ErrorCode.UNKNOWN_ERROR, ex.getErrorCode());
    }

    @Test
    @DisplayName("시계열 KPI 통계 조회 - 정상 케이스")
    void getTimeseriesStatistics_success() {
        // given
        int year = 2025;
        List<KpiTimeseriesMonthlyDto> mockList = Arrays.asList(
                new KpiTimeseriesMonthlyDto(1, 10, 4, 64.0),
                new KpiTimeseriesMonthlyDto(2, 12, 6, 71.5)
        );

        when(kpiStatisticsMapper.getTimeseriesStatistics(year)).thenReturn(mockList);

        // when
        KpiTimeseriesResponseDto result = kpiStatisticsService.getTimeseriesStatistics(year);

        // then
        assertNotNull(result);
        assertEquals(2025, result.getYear());
        assertEquals(2, result.getMonthlyStats().size());
        assertEquals(10, result.getMonthlyStats().get(0).getTotalKpiCount());
    }

    @Test
    @DisplayName("시계열 KPI 통계 조회 실패 - null 결과")
    void getTimeseriesStatistics_null_throwsException() {
        // given
        when(kpiStatisticsMapper.getTimeseriesStatistics(anyInt())).thenReturn(null);

        // when & then
        KpiException ex = assertThrows(KpiException.class, () -> {
            kpiStatisticsService.getTimeseriesStatistics(2025);
        });

        assertEquals(ErrorCode.STATISTICS_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    @DisplayName("시계열 KPI 통계 조회 실패 - 빈 결과")
    void getTimeseriesStatistics_empty_throwsException() {
        // given
        when(kpiStatisticsMapper.getTimeseriesStatistics(anyInt())).thenReturn(Collections.emptyList());

        // when & then
        KpiException ex = assertThrows(KpiException.class, () -> {
            kpiStatisticsService.getTimeseriesStatistics(2025);
        });

        assertEquals(ErrorCode.STATISTICS_NOT_FOUND, ex.getErrorCode());
    }
}
