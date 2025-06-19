package com.dao.momentum.evaluation.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.exception.KpiException;
import com.dao.momentum.evaluation.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.query.mapper.KpiStatisticsMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
