package com.dao.momentum.evaluation.kpi.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiEmployeeSummaryRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.*;
import com.dao.momentum.evaluation.kpi.query.service.KpiQueryService;
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
import java.util.Optional;

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

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("KPI 전체 내역 조회")
    @WithMockUser(username = "123", authorities = "MASTER")
    void getKpiList_success() throws Exception {
        // given
        KpiListResponseDto item = KpiListResponseDto.builder()
                .kpiId(1L)
                .empNo("HR123")
                .employeeName("김예진")
                .departmentName("인사팀")
                .positionName("대리")
                .goal("신입 채용 완료")
                .goalValue(3)
                .kpiProgress(80)
                .statusName("ACCEPTED")
                .createdAt("2025-06-01")
                .deadline("2025-06-30")
                .build();

        Pagination pagination = Pagination.builder()
                .currentPage(1)
                .totalPage(1)
                .totalItems(1)
                .build();

        KpiListResultDto resultDto = new KpiListResultDto(List.of(item), pagination);

        Mockito.when(employeeRepository.findByEmpId(123L))
                .thenReturn(Optional.of(
                        com.dao.momentum.organization.employee.command.domain.aggregate.Employee.builder()
                                .empNo("HR123")
                                .build()
                ));

        Mockito.when(kpiQueryService.getKpiListWithAccessControl(any(KpiListRequestDto.class), Mockito.eq(123L), Mockito.eq("HR123")))
                .thenReturn(resultDto);

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
    @DisplayName("KPI 세부 조회")
    @WithMockUser(authorities = "MASTER")
    void getKpiDetail_success() throws Exception {
        Long kpiId = 101L;

        KpiDetailResponseDto dto = KpiDetailResponseDto.builder()
                .kpiId(kpiId)
                .empNo("HR001")
                .employeeName("정예준")
                .departmentName("기획팀")
                .positionName("대리")
                .goal("리포트 자동화")
                .goalValue(5)
                .kpiProgress(75)
                .progress25("템플릿 작성")
                .progress50("API 연동")
                .progress75("리포트 생성")
                .progress100("운영 반영")
                .statusType("ACCEPTED")
                .createdAt("2025-05-01")
                .deadline("2025-06-30")
                .build();

        Mockito.when(kpiQueryService.getKpiDetail(kpiId)).thenReturn(dto);

        mockMvc.perform(get("/kpi/{kpiId}", kpiId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.kpiId").value(101))
                .andExpect(jsonPath("$.data.employeeName").value("정예준"))
                .andDo(print());
    }

    @Test
    @DisplayName("사원별 KPI 요약 조회")
    @WithMockUser(authorities = "HR")
    void getEmployeeKpiSummaries_success() throws Exception {
        // given
        KpiEmployeeSummaryResponseDto item = KpiEmployeeSummaryResponseDto.builder()
                .empNo("HR001")
                .employeeName("홍길동")
                .departmentName("기획팀")
                .positionName("대리")
                .totalKpiCount(6)
                .completedKpiCount(4)
                .averageProgress(76.5)
                .completionRate(66.7)
                .build();

        Pagination pagination = Pagination.builder()
                .currentPage(1)
                .totalPage(1)
                .totalItems(1)
                .build();

        KpiEmployeeSummaryResultDto resultDto = new KpiEmployeeSummaryResultDto(List.of(item), pagination);
        Mockito.when(kpiQueryService.getEmployeeKpiSummaries(any(KpiEmployeeSummaryRequestDto.class)))
                .thenReturn(resultDto);

        // when & then
        mockMvc.perform(get("/kpi/employee-summary")
                        .param("year", "2025")
                        .param("month", "6")
                        .param("deptId", "10")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].empNo").value("HR001"))
                .andExpect(jsonPath("$.data.content[0].employeeName").value("홍길동"))
                .andExpect(jsonPath("$.data.content[0].departmentName").value("기획팀"))
                .andExpect(jsonPath("$.data.content[0].positionName").value("대리"))
                .andExpect(jsonPath("$.data.content[0].totalKpiCount").value(6))
                .andExpect(jsonPath("$.data.content[0].completedKpiCount").value(4))
                .andExpect(jsonPath("$.data.content[0].averageProgress").value(76.5))
                .andExpect(jsonPath("$.data.content[0].completionRate").value(66.7))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(1))
                .andDo(print());
    }
}
