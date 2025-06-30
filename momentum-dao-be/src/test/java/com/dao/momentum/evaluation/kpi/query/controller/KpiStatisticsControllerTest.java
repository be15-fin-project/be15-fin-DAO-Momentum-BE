package com.dao.momentum.evaluation.kpi.query.controller;

import com.dao.momentum.evaluation.kpi.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiTimeseriesRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiTimeseriesMonthlyDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiTimeseriesResponseDto;
import com.dao.momentum.evaluation.kpi.query.service.KpiStatisticsService;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(KpiStatisticsController.class)
class KpiStatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KpiStatisticsService kpiStatisticsService;

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("KPI 통계 조회 - 관리자")
    @WithMockUser(username = "1", authorities = "MASTER")
    void getKpiStatistics_admin_success() throws Exception {
        KpiStatisticsResponseDto responseDto = KpiStatisticsResponseDto.builder()
                .totalKpiCount(5)
                .completedKpiCount(3)
                .averageProgress(72.4)
                .build();

        Mockito.when(kpiStatisticsService.getStatisticsWithAccessControl(any(KpiStatisticsRequestDto.class), eq(1L)))
                .thenReturn(responseDto);

        mockMvc.perform(get("/kpi/statistics")
                        .param("year", "2025")
                        .param("month", "6")
                        .param("deptId", "10")
                        .param("empId", "22"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalKpiCount").value(5))
                .andExpect(jsonPath("$.data.completedKpiCount").value(3))
                .andExpect(jsonPath("$.data.averageProgress").value(72.4))
                .andDo(print());
    }

    @Test
    @DisplayName("KPI 통계 조회 - 일반 사용자")
    @WithMockUser(username = "15", authorities = "MEMBER")
    void getKpiStatistics_user_success() throws Exception {
        KpiStatisticsResponseDto responseDto = KpiStatisticsResponseDto.builder()
                .totalKpiCount(4)
                .completedKpiCount(2)
                .averageProgress(60.0)
                .build();

        Mockito.when(kpiStatisticsService.getStatisticsWithAccessControl(any(KpiStatisticsRequestDto.class), eq(15L)))
                .thenReturn(responseDto);

        mockMvc.perform(get("/kpi/statistics")
                        .param("year", "2025")
                        .param("month", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalKpiCount").value(4))
                .andExpect(jsonPath("$.data.completedKpiCount").value(2))
                .andExpect(jsonPath("$.data.averageProgress").value(60.0))
                .andDo(print());
    }

    @Test
    @DisplayName("KPI 시계열 통계 조회 - 기본 연도")
    @WithMockUser(username = "33", authorities = "HR")
    void getTimeseriesStatistics_defaultYear() throws Exception {
        KpiTimeseriesMonthlyDto monthDto = KpiTimeseriesMonthlyDto.builder()
                .month(6)
                .totalKpiCount(10)
                .completedKpiCount(8)
                .averageProgress(85.0)
                .build();

        KpiTimeseriesResponseDto mockResponse = KpiTimeseriesResponseDto.builder()
                .year(2025)
                .monthlyStats(List.of(monthDto))
                .build();

        Mockito.when(kpiStatisticsService.getTimeseriesWithAccessControl(any(KpiTimeseriesRequestDto.class), eq(33L)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/kpi/timeseries")
                        .param("year", "2025")
                        .param("deptId", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.year").value(2025))
                .andExpect(jsonPath("$.data.monthlyStats", hasSize(1)))
                .andExpect(jsonPath("$.data.monthlyStats[0].month").value(6))
                .andExpect(jsonPath("$.data.monthlyStats[0].totalKpiCount").value(10))
                .andExpect(jsonPath("$.data.monthlyStats[0].completedKpiCount").value(8))
                .andExpect(jsonPath("$.data.monthlyStats[0].averageProgress").value(85.0))
                .andDo(print());
    }

    @Test
    @DisplayName("자신의 KPI 통계 조회 - 로그인 사용자")
    @WithMockUser(username = "50", authorities = "MEMBER")
    void getMyKpiStatistics_success() throws Exception {
        KpiStatisticsResponseDto responseDto = KpiStatisticsResponseDto.builder()
                .totalKpiCount(6)
                .completedKpiCount(5)
                .averageProgress(92.0)
                .build();

        Mockito.when(kpiStatisticsService.getStatisticsWithAccessControl(any(KpiStatisticsRequestDto.class), eq(50L)))
                .thenReturn(responseDto);

        mockMvc.perform(get("/kpi/my-statistics")
                        .param("year", "2025")
                        .param("month", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalKpiCount").value(6))
                .andExpect(jsonPath("$.data.completedKpiCount").value(5))
                .andExpect(jsonPath("$.data.averageProgress").value(92.0))
                .andDo(print());
    }

    @Test
    @DisplayName("자신의 KPI 시계열 통계 조회 - 로그인 사용자")
    @WithMockUser(username = "50", authorities = "MEMBER")
    void getMyTimeseriesStatistics_success() throws Exception {
        KpiTimeseriesMonthlyDto monthDto = KpiTimeseriesMonthlyDto.builder()
                .month(5)
                .totalKpiCount(4)
                .completedKpiCount(3)
                .averageProgress(70.0)
                .build();

        KpiTimeseriesResponseDto mockResponse = KpiTimeseriesResponseDto.builder()
                .year(2025)
                .monthlyStats(List.of(monthDto))
                .build();

        Mockito.when(kpiStatisticsService.getTimeseriesWithAccessControl(any(KpiTimeseriesRequestDto.class), eq(50L)))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/kpi/my-timeseries")
                        .param("year", "2025"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.year").value(2025))
                .andExpect(jsonPath("$.data.monthlyStats", hasSize(1)))
                .andExpect(jsonPath("$.data.monthlyStats[0].month").value(5))
                .andExpect(jsonPath("$.data.monthlyStats[0].totalKpiCount").value(4))
                .andExpect(jsonPath("$.data.monthlyStats[0].completedKpiCount").value(3))
                .andExpect(jsonPath("$.data.monthlyStats[0].averageProgress").value(70.0))
                .andDo(print());
    }

}
