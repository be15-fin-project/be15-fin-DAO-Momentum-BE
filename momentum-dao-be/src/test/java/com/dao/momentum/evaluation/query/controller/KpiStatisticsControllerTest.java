package com.dao.momentum.evaluation.query.controller;

import com.dao.momentum.evaluation.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.query.dto.request.KpiTimeseriesRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.query.dto.response.KpiTimeseriesMonthlyDto;
import com.dao.momentum.evaluation.query.dto.response.KpiTimeseriesResponseDto;
import com.dao.momentum.evaluation.query.service.KpiStatisticsService;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.isNull;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @MockitoBean
    EmployeeRepository employeeRepository;

    @Test
    @DisplayName("KPI 통계 조회 - 관리자 권한")
    @WithMockUser(username = "1", authorities = "MASTER")
    void getKpiStatistics_admin_success() throws Exception {
        // given
        KpiStatisticsResponseDto responseDto = new KpiStatisticsResponseDto();
        responseDto.setTotalKpiCount(15);
        responseDto.setCompletedKpiCount(10);
        responseDto.setAverageProgress(73.3);

        Mockito.when(kpiStatisticsService.getStatistics(any(KpiStatisticsRequestDto.class)))
                .thenReturn(responseDto);

        // when & then
        mockMvc.perform(
                        get("/kpi/statistics")
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
    @DisplayName("KPI 통계 조회 - 일반 사용자")
    @WithMockUser(username = "23", authorities = "EMPLOYEE")
    void getKpiStatistics_user_success() throws Exception {
        // given
        Employee mockEmp = Employee.builder()
                .empId(23L)
                .empNo("EMP0023")
                .build();

        Mockito.when(employeeRepository.findByEmpId(eq(23L)))
                .thenReturn(Optional.of(mockEmp));

        KpiStatisticsResponseDto responseDto = new KpiStatisticsResponseDto();
        responseDto.setTotalKpiCount(9);
        responseDto.setCompletedKpiCount(4);
        responseDto.setAverageProgress(68.8);

        Mockito.when(kpiStatisticsService.getStatistics(any(KpiStatisticsRequestDto.class)))
                .thenReturn(responseDto);

        // when & then
        mockMvc.perform(get("/kpi/statistics")
                        .param("year", "2025")
                        .param("month", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalKpiCount").value(9))
                .andExpect(jsonPath("$.data.completedKpiCount").value(4))
                .andExpect(jsonPath("$.data.averageProgress").value(68.8))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "1", authorities = {"MASTER"})
    @DisplayName("KPI 시계열 통계 조회 - year 파라미터 없음")
    void getTimeseriesStatistics_defaultYear() throws Exception {
        // given
        int thisYear = LocalDate.now().getYear();

        List<KpiTimeseriesMonthlyDto> monthlyStats = List.of(
                new KpiTimeseriesMonthlyDto(6, 12, 8, 70.0)
        );

        KpiTimeseriesResponseDto mockResponse = new KpiTimeseriesResponseDto();
        mockResponse.setYear(thisYear);
        mockResponse.setMonthlyStats(monthlyStats);

        // dto 파라미터 구성
        KpiTimeseriesRequestDto mockDto = new KpiTimeseriesRequestDto(); // year는 null
        // empId는 컨트롤러에서 설정됨 (권한자이므로 null 유지)

        Mockito.when(kpiStatisticsService.getTimeseriesStatistics(any(KpiTimeseriesRequestDto.class)))
                .thenReturn(mockResponse);

        // when & then
        mockMvc.perform(get("/kpi/timeseries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.year").value(thisYear))
                .andExpect(jsonPath("$.data.monthlyStats").isArray())
                .andDo(print());
    }


}
