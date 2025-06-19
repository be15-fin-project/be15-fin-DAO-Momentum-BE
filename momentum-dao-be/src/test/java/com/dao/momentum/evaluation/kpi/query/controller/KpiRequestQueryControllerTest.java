package com.dao.momentum.evaluation.kpi.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiRequestListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiRequestListResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiRequestListResultDto;
import com.dao.momentum.evaluation.kpi.query.service.KpiRequestQueryService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(KpiRequestQueryController.class)
class KpiRequestQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KpiRequestQueryService kpiRequestQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("KPI 요청 목록 조회 - 성공")
    @WithMockUser(username = "11", authorities = "MANAGER")
    void getKpiRequests_success() throws Exception {
        // given
        KpiRequestListResponseDto dto = new KpiRequestListResponseDto();
        dto.setKpiId(101L);
        dto.setEmpNo("20240001");
        dto.setEmployeeName("김현우");
        dto.setDepartmentName("기획팀");
        dto.setPositionName("대리");
        dto.setGoal("월간 영업 건수 10건 달성");
        dto.setGoalValue(10);
        dto.setKpiProgress(40);
        dto.setStatusName("대기중");
        dto.setCreatedAt("2025-06-01");
        dto.setDeadline("2025-06-30");

        Pagination pagination = Pagination.builder()
                .currentPage(1)
                .totalPage(1)
                .totalItems(1)
                .build();

        KpiRequestListResultDto result = new KpiRequestListResultDto(List.of(dto), pagination);

        Mockito.when(kpiRequestQueryService.getKpiRequests(eq(11L), any(KpiRequestListRequestDto.class)))
                .thenReturn(result);

        // when & then
        mockMvc.perform(get("/kpi/requests")
                        .param("statusId", "2")
                        .param("completed", "false")
                        .param("startDate", "2025-06-01")
                        .param("endDate", "2025-06-30")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].kpiId").value(101))
                .andExpect(jsonPath("$.data.content[0].empNo").value("20240001"))
                .andExpect(jsonPath("$.data.content[0].employeeName").value("김현우"))
                .andExpect(jsonPath("$.data.content[0].goalValue").value(10))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(1))
                .andDo(print());
    }
}
