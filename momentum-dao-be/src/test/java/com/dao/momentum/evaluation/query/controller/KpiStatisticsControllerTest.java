package com.dao.momentum.evaluation.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiStatisticsResponseDto;
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

    @DisplayName("KPI 통계 조회 - 성공 케이스")
    @WithMockUser(authorities = "MASTER")
    @Test
    void getKpiStatistics_success() throws Exception {
        // given
        KpiStatisticsResponseDto responseDto = new KpiStatisticsResponseDto();
        responseDto.setTotalKpiCount(15);
        responseDto.setCompletedKpiCount(10);
        responseDto.setAverageProgress(73.3);

        Mockito.when(kpiStatisticsService.getStatistics(Mockito.any(KpiStatisticsRequestDto.class)))
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
}
