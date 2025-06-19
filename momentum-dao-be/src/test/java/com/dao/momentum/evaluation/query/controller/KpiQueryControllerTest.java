package com.dao.momentum.evaluation.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.exception.KpiException;
import static com.dao.momentum.common.exception.ErrorCode.KPI_EMPLOYEE_SUMMARY_NOT_FOUND;
import com.dao.momentum.evaluation.query.dto.request.KpiEmployeeSummaryRequestDto;
import com.dao.momentum.evaluation.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.query.dto.response.*;
import com.dao.momentum.evaluation.query.service.KpiQueryService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
    @DisplayName("KPI 세부 조회")
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

    @Test
    @DisplayName("사원별 KPI 요약 조회")
    @WithMockUser(authorities = "HR")
    void getEmployeeKpiSummaries_success() throws Exception {
        // given
        KpiEmployeeSummaryResponseDto item = new KpiEmployeeSummaryResponseDto();
        item.setEmpNo("HR001");
        item.setEmployeeName("홍길동");
        item.setDepartmentName("기획팀");
        item.setPositionName("대리");
        item.setTotalKpiCount(6);
        item.setCompletedKpiCount(4);
        item.setAverageProgress(76.5);
        item.setCompletionRate(66.7);

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
