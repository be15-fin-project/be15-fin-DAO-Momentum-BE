package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiTimeseriesRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiTimeseriesMonthlyDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiTimeseriesResponseDto;
import com.dao.momentum.evaluation.kpi.query.mapper.KpiStatisticsMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        KpiStatisticsRequestDto requestDto = KpiStatisticsRequestDto.builder()
                .year(2025)
                .month(6)
                .deptId(101L)
                .build();

        KpiStatisticsResponseDto mockResponse = KpiStatisticsResponseDto.builder()
                .totalKpiCount(12)
                .completedKpiCount(5)
                .averageProgress(66.6)
                .build();

        when(kpiStatisticsMapper.getMonthlyStatistics(requestDto)).thenReturn(mockResponse);

        KpiStatisticsResponseDto result = kpiStatisticsService.getStatistics(requestDto);

        assertNotNull(result);
        assertEquals(12, result.getTotalKpiCount());
        assertEquals(5, result.getCompletedKpiCount());
        assertEquals(66.6, result.getAverageProgress());
    }

    @Test
    @DisplayName("KPI 통계 조회 실패 - 결과 없음")
    void getStatistics_nullResponse_throwsException() {
        KpiStatisticsRequestDto requestDto = KpiStatisticsRequestDto.builder()
                .year(2025)
                .month(6)
                .build();

        when(kpiStatisticsMapper.getMonthlyStatistics(requestDto)).thenReturn(null);

        KpiException ex = assertThrows(KpiException.class, () -> {
            kpiStatisticsService.getStatistics(requestDto);
        });

        assertEquals(ErrorCode.UNKNOWN_ERROR, ex.getErrorCode());
    }

    @Test
    @DisplayName("시계열 KPI 통계 조회 - 정상 케이스")
    void getTimeseriesStatistics_success() {
        KpiTimeseriesRequestDto requestDto = KpiTimeseriesRequestDto.builder()
                .year(2025)
                .empNo("20250001")
                .build();

        List<KpiTimeseriesMonthlyDto> mockList = List.of(
                KpiTimeseriesMonthlyDto.builder().month(1).totalKpiCount(10).completedKpiCount(4).averageProgress(64.0).build(),
                KpiTimeseriesMonthlyDto.builder().month(2).totalKpiCount(12).completedKpiCount(6).averageProgress(71.5).build()
        );

        when(kpiStatisticsMapper.getTimeseriesStatistics(any())).thenReturn(mockList);

        KpiTimeseriesResponseDto result = kpiStatisticsService.getTimeseriesStatistics(requestDto);

        assertNotNull(result);
        assertEquals(2025, result.getYear());
        assertEquals(2, result.getMonthlyStats().size());
        assertEquals(10, result.getMonthlyStats().get(0).getTotalKpiCount());
    }

    @Test
    @DisplayName("시계열 KPI 통계 조회 실패 - null 결과")
    void getTimeseriesStatistics_null_throwsException() {
        KpiTimeseriesRequestDto requestDto = KpiTimeseriesRequestDto.builder()
                .year(2025)
                .empNo("20250001")
                .build();

        when(kpiStatisticsMapper.getTimeseriesStatistics(any())).thenReturn(null);

        KpiException ex = assertThrows(KpiException.class, () -> {
            kpiStatisticsService.getTimeseriesStatistics(requestDto);
        });

        assertEquals(ErrorCode.STATISTICS_NOT_FOUND, ex.getErrorCode());
    }
}
