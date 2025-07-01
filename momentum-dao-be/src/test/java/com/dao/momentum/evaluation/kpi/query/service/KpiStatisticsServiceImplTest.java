package com.dao.momentum.evaluation.kpi.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.evaluation.kpi.exception.KpiException;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiTimeseriesRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiTimeseriesMonthlyDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiTimeseriesResponseDto;
import com.dao.momentum.evaluation.kpi.query.mapper.KpiStatisticsMapper;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KpiStatisticsServiceImplTest {

    @Mock
    private KpiStatisticsMapper kpiStatisticsMapper;
    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private KpiStatisticsServiceImpl kpiStatisticsService;

    @BeforeEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("KPI 통계 정상 조회")
    void getStatistics_success() {
        KpiStatisticsRequestDto requestDto = KpiStatisticsRequestDto.builder()
                .year(2025).month(6).deptId(101L).build();

        KpiStatisticsResponseDto mockResponse = KpiStatisticsResponseDto.builder()
                .totalKpiCount(12).completedKpiCount(5).averageProgress(66.6).build();

        when(kpiStatisticsMapper.getMonthlyStatistics(requestDto)).thenReturn(mockResponse);

        KpiStatisticsResponseDto result = kpiStatisticsService.getStatistics(requestDto);

        assertThat(result).isNotNull();
        assertThat(result.getTotalKpiCount()).isEqualTo(12);
    }

    @Test
    @DisplayName("KPI 통계 조회 실패 - 결과 없음")
    void getStatistics_nullResponse_throwsException() {
        KpiStatisticsRequestDto requestDto = KpiStatisticsRequestDto.builder()
                .year(2025).month(6).build();

        when(kpiStatisticsMapper.getMonthlyStatistics(requestDto)).thenReturn(null);

        KpiException ex = assertThrows(KpiException.class, () -> {
            kpiStatisticsService.getStatistics(requestDto);
        });

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.UNKNOWN_ERROR);
    }

    @Test
    @DisplayName("시계열 KPI 통계 조회 - 정상 케이스")
    void getTimeseriesStatistics_success() {
        KpiTimeseriesRequestDto requestDto = KpiTimeseriesRequestDto.builder()
                .year(2025).empNo("20250001").build();

        List<KpiTimeseriesMonthlyDto> mockList = List.of(
                KpiTimeseriesMonthlyDto.builder().month(1).totalKpiCount(10).completedKpiCount(4).averageProgress(64.0).build(),
                KpiTimeseriesMonthlyDto.builder().month(2).totalKpiCount(12).completedKpiCount(6).averageProgress(71.5).build()
        );

        when(kpiStatisticsMapper.getTimeseriesStatistics(any())).thenReturn(mockList);

        KpiTimeseriesResponseDto result = kpiStatisticsService.getTimeseriesStatistics(requestDto);

        assertThat(result.getYear()).isEqualTo(2025);
        assertThat(result.getMonthlyStats()).hasSize(2);
    }

    @Test
    @DisplayName("시계열 KPI 통계 조회 실패 - 결과 없음")
    void getTimeseriesStatistics_null_throwsException() {
        KpiTimeseriesRequestDto requestDto = KpiTimeseriesRequestDto.builder()
                .year(2025).empNo("20250001").build();

        when(kpiStatisticsMapper.getTimeseriesStatistics(any())).thenReturn(null);

        KpiException ex = assertThrows(KpiException.class, () -> {
            kpiStatisticsService.getTimeseriesStatistics(requestDto);
        });

        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.STATISTICS_NOT_FOUND);
    }

    @Test
    @DisplayName("자신 KPI 통계 조회 - 일반 사용자")
    void getStatisticsWithControl_user() {
        when(employeeRepository.findEmpNoByEmpId(100L)).thenReturn("20250001");

        when(kpiStatisticsMapper.getMonthlyStatistics(any()))
                .thenReturn(KpiStatisticsResponseDto.builder().totalKpiCount(6).completedKpiCount(4).averageProgress(70.0).build());

        KpiStatisticsResponseDto result = kpiStatisticsService.getStatisticsWithControl(
                KpiStatisticsRequestDto.builder().year(2025).month(6).build(),
                100L
        );

        assertThat(result).isNotNull();
        assertThat(result.getTotalKpiCount()).isEqualTo(6);
    }

    @Test
    @DisplayName("자신 KPI 시계열 통계 조회 - 일반 사용자")
    void getTimeseriesWithControl_user() {
        when(employeeRepository.findEmpNoByEmpId(100L)).thenReturn("20250001");

        when(kpiStatisticsMapper.getTimeseriesStatistics(any()))
                .thenReturn(List.of(KpiTimeseriesMonthlyDto.builder().month(5).totalKpiCount(5).completedKpiCount(4).averageProgress(80.0).build()));

        KpiTimeseriesResponseDto result = kpiStatisticsService.getTimeseriesWithControl(
                KpiTimeseriesRequestDto.builder().year(2025).build(),
                100L
        );

        assertThat(result.getMonthlyStats()).hasSize(1);
    }

    @Test
    @DisplayName("권한 있는 사용자 KPI 통계 조회")
    void getStatisticsWithAccessControl_privileged() {
        setSecurityContextWithRoles("MASTER");

        KpiStatisticsRequestDto dto = KpiStatisticsRequestDto.builder()
                .year(2025).month(6).empNo("20250099").build();

        when(kpiStatisticsMapper.getMonthlyStatistics(any()))
                .thenReturn(KpiStatisticsResponseDto.builder().totalKpiCount(3).completedKpiCount(2).averageProgress(50.0).build());

        KpiStatisticsResponseDto result = kpiStatisticsService.getStatisticsWithAccessControl(dto, 101L);

        assertThat(result.getTotalKpiCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("권한 없는 사용자 KPI 시계열 조회 - empId 기준")
    void getTimeseriesWithAccessControl_unprivileged() {
        setSecurityContextWithRoles("EMPLOYEE");

        when(employeeRepository.findEmpNoByEmpId(101L)).thenReturn("20250077");
        when(kpiStatisticsMapper.getTimeseriesStatistics(any()))
                .thenReturn(List.of(KpiTimeseriesMonthlyDto.builder().month(6).totalKpiCount(5).completedKpiCount(3).averageProgress(60.0).build()));

        KpiTimeseriesRequestDto dto = KpiTimeseriesRequestDto.builder()
                .year(2025).deptId(10L).positionId(2).build();

        KpiTimeseriesResponseDto result = kpiStatisticsService.getTimeseriesWithAccessControl(dto, 101L);

        assertThat(result.getMonthlyStats()).hasSize(1);
    }

    private void setSecurityContextWithRoles(String... roles) {
        List<SimpleGrantedAuthority> authorities =
                List.of(roles).stream().map(SimpleGrantedAuthority::new).toList();
        TestingAuthenticationToken auth = new TestingAuthenticationToken("user", "pass", authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
