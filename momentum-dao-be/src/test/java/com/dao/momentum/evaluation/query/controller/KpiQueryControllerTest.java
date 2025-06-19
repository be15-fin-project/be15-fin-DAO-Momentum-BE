package com.dao.momentum.evaluation.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiListResponseDto;
import com.dao.momentum.evaluation.query.dto.response.KpiDetailResponseDto;
import com.dao.momentum.evaluation.query.dto.response.KpiListResultDto;
import com.dao.momentum.evaluation.query.service.KpiQueryService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(KpiQueryController.class)
class KpiQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KpiQueryService kpiQueryService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("KPI 전체 내역 조회 - 성공 케이스")
    @WithMockUser(authorities = "MASTER")
    void getKpiList_success() throws Exception {
        // given
        KpiListResponseDto item = new KpiListResponseDto();
        item.setKpiId(1L);
        item.setEmpNo("HR123");
        item.setEmployeeName("김예진");
        item.setDepartmentName("인사팀");
        item.setPositionName("대리");
        item.setGoal("신입 채용 완료");
        item.setGoalValue(3);
        item.setKpiProgress(80);
        item.setStatusName("ACCEPTED");
        item.setCreatedAt("2025-06-01");
        item.setDeadline("2025-06-30");

        Pagination pagination = Pagination.builder()
                .currentPage(1)
                .totalPage(1)
                .totalItems(1)
                .build();

        KpiListResultDto resultDto = new KpiListResultDto(List.of(item), pagination);

        Mockito.when(kpiQueryService.getKpiList(any(KpiListRequestDto.class))).thenReturn(resultDto);

        // when & then
        mockMvc.perform(get("/kpi/list")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].empNo").value("HR123"))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("KPI 세부 조회 - 성공")
    @WithMockUser(authorities = "MASTER")
    void getKpiDetail_success() throws Exception {
        Long kpiId = 101L;

        KpiDetailResponseDto dto = new KpiDetailResponseDto();
        dto.setKpiId(kpiId);
        dto.setEmpNo("HR001");
        dto.setEmployeeName("정예준");
        dto.setDepartmentName("기획팀");
        dto.setPositionName("대리");
        dto.setGoal("리포트 자동화");
        dto.setGoalValue(5);
        dto.setKpiProgress(75);
        dto.setProgress25("템플릿 작성");
        dto.setProgress50("API 연동");
        dto.setProgress75("리포트 생성");
        dto.setProgress100("운영 반영");
        dto.setStatusType("ACCEPTED");
        dto.setCreatedAt("2025-05-01");
        dto.setDeadline("2025-06-30");

        Mockito.when(kpiQueryService.getKpiDetail(kpiId)).thenReturn(dto);

        mockMvc.perform(get("/kpi/{kpiId}", kpiId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.kpiId").value(101))
                .andExpect(jsonPath("$.data.employeeName").value("정예준"))
                .andDo(print());
    }

}
