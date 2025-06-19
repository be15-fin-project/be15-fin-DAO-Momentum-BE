package com.dao.momentum.evaluation.query.controller;

import com.dao.momentum.evaluation.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.query.dto.response.KpiTimeseriesMonthlyDto;
import com.dao.momentum.evaluation.query.dto.response.KpiTimeseriesResponseDto;
import com.dao.momentum.evaluation.query.service.KpiStatisticsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(KpiStatisticsController.class)
class KpiStatisticsControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    KpiStatisticsService kpiStatisticsService;

    @Test
    @DisplayName("KPI 통계 조회 - 성공 케이스")
    @WithMockUser(authorities = "MASTER")
    void getKpiStatistics_success() throws Exception {
        // given
        KpiStatisticsResponseDto responseDto = new KpiStatisticsResponseDto();
        responseDto.setTotalKpiCount(15);
        responseDto.setCompletedKpiCount(10);
        responseDto.setAverageProgress(73.3);

        Mockito.when(kpiStatisticsService.getStatistics(any(KpiStatisticsRequestDto.class)))
                .thenReturn(responseDto);

        // when & then
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/kpi/statistics")
                                .param("year", "2025")
                                .param("month", "6")
                                .param("deptId", "101")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalKpiCount").value(15))
                .andExpect(jsonPath("$.data.completedKpiCount").value(10))
                .andExpect(jsonPath("$.data.averageProgress").value(73.3))
                .andExpect(jsonPath("$.timestamp").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("KPI 시계열 통계 조회 - year 파라미터 없음")
    @WithMockUser(authorities = "MASTER")
    void getTimeseriesStatistics_defaultYear() throws Exception {
        // given
        List<KpiTimeseriesMonthlyDto> monthlyStats = Arrays.asList(
                new KpiTimeseriesMonthlyDto(1, 12, 5, 62.5),
                new KpiTimeseriesMonthlyDto(2, 8, 3, 71.0)
        );
        KpiTimeseriesResponseDto response = new KpiTimeseriesResponseDto(2025, monthlyStats);

        Mockito.when(kpiStatisticsService.getTimeseriesStatistics(null)).thenReturn(response);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/kpi/timeseries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2025))
                .andExpect(jsonPath("$.monthlyStats").isArray())
                .andExpect(jsonPath("$.monthlyStats[0].month").value(1))
                .andExpect(jsonPath("$.monthlyStats[0].totalKpiCount").value(12))
                .andExpect(jsonPath("$.monthlyStats[0].completedKpiCount").value(5))
                .andExpect(jsonPath("$.monthlyStats[0].averageProgress").value(62.5))
                .andDo(print());
    }

    @Test
    @DisplayName("KPI 시계열 통계 조회 - year=2024")
    @WithMockUser(authorities = "MASTER")
    void getTimeseriesStatistics_specificYear() throws Exception {
        // given
        List<KpiTimeseriesMonthlyDto> monthlyStats = List.of(
                new KpiTimeseriesMonthlyDto(3, 9, 4, 68.8)
        );
        KpiTimeseriesResponseDto response = new KpiTimeseriesResponseDto(2024, monthlyStats);

        Mockito.when(kpiStatisticsService.getTimeseriesStatistics(2024)).thenReturn(response);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/kpi/timeseries")
                        .param("year", "2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.year").value(2024))
                .andExpect(jsonPath("$.monthlyStats[0].month").value(3))
                .andExpect(jsonPath("$.monthlyStats[0].totalKpiCount").value(9))
                .andExpect(jsonPath("$.monthlyStats[0].completedKpiCount").value(4))
                .andExpect(jsonPath("$.monthlyStats[0].averageProgress").value(68.8))
                .andDo(print());
    }
}
