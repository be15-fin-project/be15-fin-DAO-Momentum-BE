package com.dao.momentum.evaluation.kpi.query.controller;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiEmployeeSummaryRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.*;
import com.dao.momentum.evaluation.kpi.query.service.KpiQueryService;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
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

    @Test
    @DisplayName("KPI 전체 내역 조회")
    @WithMockUser(username = "123", authorities = "MASTER")
    void getKpiList_success() throws Exception {
        Long empId = 123L;
        String empNo = "HR123";

        KpiListResponseDto item = KpiListResponseDto.builder()
                .kpiId(1L)
                .empNo(empNo)
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

        KpiListResultDto result = new KpiListResultDto(
                List.of(item),
                Pagination.builder()
                        .currentPage(1)
                        .totalPage(1)
                        .totalItems(1)
                        .build()

        );

        Mockito.when(kpiQueryService.getKpiListWithAccessControl(any(KpiListRequestDto.class), Mockito.eq(empId)))
                .thenReturn(result);

        mockMvc.perform(get("/kpi/list")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].empNo").value(empNo))
                .andExpect(jsonPath("$.data.pagination.totalItems").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("자신의 KPI 전체 내역 조회")
    @WithMockUser(username = "123", authorities = "EMPLOYEE")
    void getMyKpiList_success() throws Exception {
        Long empId = 123L;

        KpiListResponseDto item = KpiListResponseDto.builder()
                .kpiId(2L)
                .empNo("HR123")
                .employeeName("김예진")
                .departmentName("인사팀")
                .positionName("대리")
                .goal("성과관리 도입")
                .goalValue(5)
                .kpiProgress(90)
                .statusName("IN_PROGRESS")
                .createdAt("2025-06-01")
                .deadline("2025-06-30")
                .build();

        KpiListResultDto result = new KpiListResultDto(
                List.of(item),
                Pagination.builder()
                        .currentPage(1)
                        .totalPage(1)
                        .totalItems(1)
                        .build()

        );

        Mockito.when(kpiQueryService.getKpiListWithControl(any(KpiListRequestDto.class), Mockito.eq(empId)))
                .thenReturn(result);

        mockMvc.perform(get("/kpi/my-list")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].goal").value("성과관리 도입"))
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
                .andExpect(jsonPath("$.data.kpiId").value(kpiId.intValue()))
                .andExpect(jsonPath("$.data.goal").value("리포트 자동화"))
                .andDo(print());
    }

    @Test
    @DisplayName("사원별 KPI 요약 조회")
    @WithMockUser(authorities = "HR")
    void getEmployeeKpiSummaries_success() throws Exception {
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

        KpiEmployeeSummaryResultDto result = new KpiEmployeeSummaryResultDto(
                List.of(item),
                Pagination.builder()
                        .currentPage(1)
                        .totalPage(1)
                        .totalItems(1)
                        .build()

        );

        Mockito.when(kpiQueryService.getEmployeeKpiSummaries(any(KpiEmployeeSummaryRequestDto.class)))
                .thenReturn(result);

        mockMvc.perform(get("/kpi/employee-summary")
                        .param("year", "2025")
                        .param("month", "6")
                        .param("deptId", "10")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].employeeName").value("홍길동"))
                .andExpect(jsonPath("$.data.content[0].averageProgress").value(76.5))
                .andDo(print());
    }

    @Test
    @DisplayName("KPI 대시보드 KPI 조회")
    @WithMockUser(username = "123", authorities = "EMPLOYEE")
    void getDashboardKpis_success() throws Exception {
        Long empId = 123L;

        KpiDashboardResponseDto dto = KpiDashboardResponseDto.builder()
                .kpiId(100L)
                .goal("프로세스 정비")
                .goalValue(3)
                .kpiProgress(50)
                .statusType("IN_PROGRESS")
                .createdAt("2025-07-01")
                .deadline("2025-07-31")
                .build();

        Mockito.when(kpiQueryService.getDashboardKpis(Mockito.eq(empId), any()))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/kpi/dashboard")
                        .param("startDate", "2025-07-01")
                        .param("endDate", "2025-07-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].goal").value("프로세스 정비"))
                .andDo(print());
    }
}
