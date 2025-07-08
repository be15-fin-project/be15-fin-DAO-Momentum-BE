package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiDashboardRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiEmployeeSummaryRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.*;
import com.dao.momentum.evaluation.kpi.query.mapper.KpiQueryMapper;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiQueryServiceImplTest {

    @Mock
    private KpiQueryMapper kpiQueryMapper;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private KpiQueryServiceImpl kpiQueryService;

    @Nested
    @DisplayName("KPI 전체 목록 조회 관련 테스트")
    class KpiListTests {

        @Test
        @DisplayName("KPI 전체 목록 조회 성공")
        void getKpiList_success() {
            KpiListRequestDto requestDto = KpiListRequestDto.builder()
                    .page(1)
                    .size(10)
                    .build();

            List<KpiListResponseDto> mockList = List.of(KpiListResponseDto.builder().build());
            when(kpiQueryMapper.getKpiListCount(requestDto)).thenReturn(1L);
            when(kpiQueryMapper.getKpiList(requestDto)).thenReturn(mockList);

            KpiListResultDto result = kpiQueryService.getKpiList(requestDto);

            assertNotNull(result);
            assertEquals(1, result.pagination().getTotalItems());
            assertEquals(1, result.content().size());
        }

        @Test
        @DisplayName("KPI 전체 목록 조회 실패 - null 리스트")
        void getKpiList_null_throwsException() {
            KpiListRequestDto requestDto = KpiListRequestDto.builder()
                    .page(1)
                    .size(10)
                    .build();

            when(kpiQueryMapper.getKpiListCount(requestDto)).thenReturn(0L);
            when(kpiQueryMapper.getKpiList(requestDto)).thenReturn(null);

            KpiException ex = assertThrows(KpiException.class, () -> {
                kpiQueryService.getKpiList(requestDto);
            });

            assertEquals(ErrorCode.KPI_LIST_NOT_FOUND, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("KPI 세부 조회 관련 테스트")
    class KpiDetailTests {

        @Test
        @DisplayName("KPI 세부 조회 성공")
        void getKpiDetail_success() {
            Long kpiId = 101L;
            KpiDetailResponseDto mockDetail = KpiDetailResponseDto.builder()
                    .kpiId(kpiId)
                    .build();

            when(kpiQueryMapper.getKpiDetail(kpiId)).thenReturn(mockDetail);

            KpiDetailResponseDto result = kpiQueryService.getKpiDetail(kpiId);

            assertNotNull(result);
            assertEquals(101L, result.kpiId());
        }

        @Test
        @DisplayName("KPI 세부 조회 실패 - null")
        void getKpiDetail_null_throwsException() {
            Long kpiId = 101L;

            when(kpiQueryMapper.getKpiDetail(kpiId)).thenReturn(null);

            KpiException ex = assertThrows(KpiException.class, () -> {
                kpiQueryService.getKpiDetail(kpiId);
            });

            assertEquals(ErrorCode.KPI_NOT_FOUND, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("사원별 KPI 요약 조회 관련 테스트")
    class EmployeeKpiSummaryTests {

        @Test
        @DisplayName("사원별 KPI 요약 조회 성공")
        void getEmployeeKpiSummaries_success() {
            KpiEmployeeSummaryRequestDto requestDto = KpiEmployeeSummaryRequestDto.builder()
                    .page(1)
                    .size(10)
                    .build();

            List<KpiEmployeeSummaryResponseDto> mockList = List.of(KpiEmployeeSummaryResponseDto.builder().build());

            when(kpiQueryMapper.countEmployeeKpiSummary(requestDto)).thenReturn(1L);
            when(kpiQueryMapper.getEmployeeKpiSummary(requestDto)).thenReturn(mockList);

            KpiEmployeeSummaryResultDto result = kpiQueryService.getEmployeeKpiSummaries(requestDto);

            assertNotNull(result);
            assertEquals(1, result.pagination().getTotalItems());
            assertEquals(1, result.content().size());
        }

        @Test
        @DisplayName("사원별 KPI 요약 조회 실패 - null 리스트")
        void getEmployeeKpiSummaries_null_throwsException() {
            KpiEmployeeSummaryRequestDto requestDto = KpiEmployeeSummaryRequestDto.builder()
                    .page(1)
                    .size(10)
                    .build();

            when(kpiQueryMapper.countEmployeeKpiSummary(requestDto)).thenReturn(0L);
            when(kpiQueryMapper.getEmployeeKpiSummary(requestDto)).thenReturn(null);

            KpiException ex = assertThrows(KpiException.class, () -> {
                kpiQueryService.getEmployeeKpiSummaries(requestDto);
            });

            assertEquals(ErrorCode.KPI_EMPLOYEE_SUMMARY_NOT_FOUND, ex.getErrorCode());
        }
    }

    @Nested
    @DisplayName("대시보드 KPI 조회 테스트")
    class DashboardKpiTests {

        @Test
        @DisplayName("대시보드 KPI 조회 성공")
        void getDashboardKpis_success() {
            Long empId = 123L;
            KpiDashboardRequestDto requestDto = KpiDashboardRequestDto.builder().build();

            List<KpiDashboardResponseDto> mockResult = List.of(KpiDashboardResponseDto.builder().build());
            when(kpiQueryMapper.getDashboardKpis(empId, requestDto)).thenReturn(mockResult);

            List<KpiDashboardResponseDto> result = kpiQueryService.getDashboardKpis(empId, requestDto);

            assertNotNull(result);
            assertEquals(1, result.size());
        }

        @Test
        @DisplayName("대시보드 KPI 조회 실패 - empId null")
        void getDashboardKpis_fail_nullEmpId() {
            KpiDashboardRequestDto requestDto = KpiDashboardRequestDto.builder().build();

            KpiException ex = assertThrows(KpiException.class, () -> {
                kpiQueryService.getDashboardKpis(null, requestDto);
            });

            assertEquals(ErrorCode.INVALID_EMPLOYEE_INFO, ex.getErrorCode());
        }
    }

}
