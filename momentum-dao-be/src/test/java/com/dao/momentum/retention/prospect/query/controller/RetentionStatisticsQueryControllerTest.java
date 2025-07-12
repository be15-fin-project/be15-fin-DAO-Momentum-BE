package com.dao.momentum.retention.prospect.query.controller;

import com.dao.momentum.retention.prospect.query.dto.request.RetentionInsightRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionStatisticsRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionTimeseriesRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionAverageScoreDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionMonthlyStatDto;
import com.dao.momentum.retention.prospect.query.dto.response.StabilityRatioByDeptDto;
import com.dao.momentum.retention.prospect.query.dto.response.StabilityRatioSummaryDto;
import com.dao.momentum.retention.prospect.query.service.RetentionStatisticsQueryService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.mockito.ArgumentMatchers.any;

@WebMvcTest(RetentionStatisticsQueryController.class)
class RetentionStatisticsQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RetentionStatisticsQueryService retentionStatisticsQueryService;

    @Test
    @DisplayName("평균 근속 지수 조회 성공")
    @WithMockUser(authorities = "HR")
    void getAverageScore_success() throws Exception {
        RetentionAverageScoreDto dto = RetentionAverageScoreDto.builder()
                .averageScore(78.4)
                .totalEmpCount(100L)
                .stabilitySafeRatio(45.0)
                .stabilityRiskRatio(12.5)
                .build();

        Mockito.when(retentionStatisticsQueryService.getAverageScore(any(RetentionStatisticsRequestDto.class)))
                .thenReturn(dto);

        mockMvc.perform(get("/retention/statistics/overview")
                        .param("year", "2025")
                        .param("month", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.averageScore").value(78.4))
                .andExpect(jsonPath("$.data.totalEmpCount").value(100))
                .andExpect(jsonPath("$.data.stabilitySafeRatio").value(45.0))
                .andExpect(jsonPath("$.data.stabilityRiskRatio").value(12.5))
                .andDo(print());
    }

    @Test
    @DisplayName("부서별 근속 안정성 분포 조회 성공")
    @WithMockUser(authorities = "MASTER")
    void getStabilityDistribution_success() throws Exception {
        StabilityRatioByDeptDto dto = StabilityRatioByDeptDto.builder()
                .deptId(10)
                .deptName("기획팀")
                .positionId(3)
                .positionName("대리")
                .empCount(100)
                .progress20(10)
                .progress40(20)
                .progress60(30)
                .progress80(25)
                .progress100(15)
                .build();

        Mockito.when(retentionStatisticsQueryService.getStabilityDistributionByDept(any(RetentionInsightRequestDto.class)))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/retention/statistics/stability-distribution")
                        .param("roundId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].deptName").value("기획팀"))
                .andExpect(jsonPath("$.data[0].positionName").value("대리"))
                .andExpect(jsonPath("$.data[0].empCount").value(100))
                .andExpect(jsonPath("$.data[0].progress20").value(10))
                .andExpect(jsonPath("$.data[0].progress40").value(20))
                .andExpect(jsonPath("$.data[0].progress60").value(30))
                .andExpect(jsonPath("$.data[0].progress80").value(25))
                .andExpect(jsonPath("$.data[0].progress100").value(15))
                .andDo(print());
    }

    @Test
    @DisplayName("전체 근속 안정성 분포 요약 조회 성공")
    @WithMockUser(authorities = "HR")
    void getOverallStabilityDistribution_success() throws Exception {
        StabilityRatioSummaryDto dto = StabilityRatioSummaryDto.builder()
                .goodCount(100L)
                .normalCount(30L)
                .warningCount(15L)
                .severeCount(9L)
                .totalCount(154L)
                .build();

        Mockito.when(retentionStatisticsQueryService.getOverallStabilityDistribution(any(RetentionInsightRequestDto.class)))
                .thenReturn(dto);

        mockMvc.perform(get("/retention/statistics/stability-distribution/overall")
                        .param("roundId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.goodCount").value(100))
                .andExpect(jsonPath("$.data.normalCount").value(30))
                .andExpect(jsonPath("$.data.warningCount").value(15))
                .andExpect(jsonPath("$.data.severeCount").value(9))
                .andExpect(jsonPath("$.data.totalCount").value(154))
                .andDo(print());
    }

    @Test
    @DisplayName("월별 근속 지수 시계열 조회 성공")
    @WithMockUser(authorities = "HR")
    void getRetentionTimeseriesStats_success() throws Exception {
        RetentionMonthlyStatDto stat1 = RetentionMonthlyStatDto.builder()
                .year(2024)
                .month(12)
                .averageScore(73.2)
                .stdDeviation(11.2)
                .build();

        RetentionMonthlyStatDto stat2 = RetentionMonthlyStatDto.builder()
                .year(2025)
                .month(1)
                .averageScore(75.0)
                .stdDeviation(9.5)
                .build();

        Mockito.when(retentionStatisticsQueryService.getMonthlyRetentionStats(any(RetentionTimeseriesRequestDto.class)))
                .thenReturn(List.of(stat1, stat2));

        mockMvc.perform(get("/retention/statistics/timeseries")
                        .param("yearFrom", "2024")
                        .param("monthFrom", "12")
                        .param("yearTo", "2025")
                        .param("monthTo", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].averageScore").value(73.2))
                .andExpect(jsonPath("$.data[0].month").value(12))
                .andExpect(jsonPath("$.data[1].averageScore").value(75.0))
                .andExpect(jsonPath("$.data[1].month").value(1))
                .andDo(print());
    }
}
