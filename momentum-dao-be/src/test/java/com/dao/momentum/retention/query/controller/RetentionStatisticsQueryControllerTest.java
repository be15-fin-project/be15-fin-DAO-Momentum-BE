package com.dao.momentum.retention.query.controller;

import com.dao.momentum.retention.prospect.query.controller.RetentionStatisticsQueryController;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionInsightRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionStatisticsRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionAverageScoreDto;
import com.dao.momentum.retention.prospect.query.dto.response.StabilityDistributionByDeptDto;
import com.dao.momentum.retention.prospect.query.service.RetentionStatisticsQueryService;
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

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("평균 근속 지수 조회 성공")
    @WithMockUser(authorities = "HR")
    void getAverageScore_success() throws Exception {
        // given
        RetentionAverageScoreDto dto = RetentionAverageScoreDto.builder()
                .averageScore(78.4)
                .build();

        Mockito.when(retentionStatisticsQueryService.getAverageScore(any(RetentionStatisticsRequestDto.class)))
                .thenReturn(dto);

        // when & then
        mockMvc.perform(get("/retention/statistics/average-score")
                        .param("year", "2025")
                        .param("month", "6")
                        .param("deptId", "3")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.averageScore").value(78.4))
                .andDo(print());
    }

    @Test
    @DisplayName("부서별 근속 안정성 분포 조회 성공")
    @WithMockUser(authorities = "MASTER")
    void getStabilityDistribution_success() throws Exception {
        // given
        StabilityDistributionByDeptDto dto = StabilityDistributionByDeptDto.builder()
                .deptName("기획팀")
                .positionName("대리")
                .empCount(20)
                .progress20(0)
                .progress40(0)
                .progress60(5)
                .progress80(10)
                .progress100(5)
                .build();

        Mockito.when(retentionStatisticsQueryService.getStabilityDistributionByDept(any(RetentionInsightRequestDto.class)))
                .thenReturn(List.of(dto));

        // when & then
        mockMvc.perform(get("/retention/statistics/stability-distribution")
                        .param("roundId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].deptName").value("기획팀"))
                .andExpect(jsonPath("$.data[0].positionName").value("대리"))
                .andExpect(jsonPath("$.data[0].empCount").value(20))
                .andExpect(jsonPath("$.data[0].progress60").value(5))
                .andExpect(jsonPath("$.data[0].progress80").value(10))
                .andExpect(jsonPath("$.data[0].progress100").value(5))
                .andDo(print());
    }

    @Test
    @DisplayName("전체 근속 안정성 비율 조회 성공 (요약)")
    @WithMockUser(authorities = "HR")
    void getOverallStabilityDistribution_success() throws Exception {
        StabilityDistributionByDeptDto dto = StabilityDistributionByDeptDto.builder()
                .deptName("전체")
                .positionName(null)
                .empCount(50)
                .progress20(2)
                .progress40(5)
                .progress60(10)
                .progress80(18)
                .progress100(15)
                .build();

        Mockito.when(retentionStatisticsQueryService.getOverallStabilityDistribution(any(RetentionInsightRequestDto.class)))
                .thenReturn(dto);

        mockMvc.perform(get("/retention/statistics/stability-distribution/overall")
                        .param("roundId", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.deptName").value("전체"))
                .andExpect(jsonPath("$.data.empCount").value(50))
                .andExpect(jsonPath("$.data.progress100").value(15))
                .andDo(print());
    }
}
