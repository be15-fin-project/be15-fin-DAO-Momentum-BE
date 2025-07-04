package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.retention.prospect.exception.ProspectException;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionInsightRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionStatisticsRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionTimeseriesRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionAverageScoreDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionMonthlyStatDto;
import com.dao.momentum.retention.prospect.query.dto.response.StabilityDistributionByDeptDto;
import com.dao.momentum.retention.prospect.query.mapper.RetentionStatisticsMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetentionStatisticsQueryServiceImplTest {

    @Mock
    private RetentionStatisticsMapper mapper;

    @InjectMocks
    private RetentionStatisticsQueryServiceImpl service;

    @Test
    @DisplayName("평균 근속 지수 - 정상 조회 (getAverageScore_success)")
    void getAverageScore_success() {
        var req = new RetentionStatisticsRequestDto();
        req.setRoundId(1);
        req.setDeptId(10);
        req.setPositionId(5);

        var dto = RetentionAverageScoreDto.builder()
                .averageScore(75.5)
                .totalEmpCount(20)
                .stabilitySafeRatio(40.0)
                .stabilityRiskRatio(10.0)
                .build();

        when(mapper.findAverageRetentionScore(req)).thenReturn(dto);

        var result = service.getAverageScore(req);

        assertThat(result.getAverageScore()).isEqualTo(75.5);
        assertThat(result.getTotalEmpCount()).isEqualTo(20);
        assertThat(result.getStabilitySafeRatio()).isEqualTo(40.0);
        assertThat(result.getStabilityRiskRatio()).isEqualTo(10.0);
    }

    @Test
    @DisplayName("평균 근속 지수 - 조회 결과 없음 (getAverageScore_empty)")
    void getAverageScore_empty() {
        var req = new RetentionStatisticsRequestDto();
        req.setRoundId(1);

        when(mapper.findAverageRetentionScore(req)).thenReturn(null);

        assertThatThrownBy(() -> service.getAverageScore(req))
                .isInstanceOf(ProspectException.class)
                .hasMessageContaining(ErrorCode.RETENTION_FORECAST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("전체 안정성 분포 - 정상 조회 (getOverallStabilityDistribution_success)")
    void getOverallStabilityDistribution_success() {
        var req = new RetentionInsightRequestDto();
        req.setRoundId(3);

        var dto = StabilityDistributionByDeptDto.builder()
                .deptName(null)
                .positionName(null)
                .empCount(100)
                .progress20(10)
                .progress40(20)
                .progress60(30)
                .progress80(25)
                .progress100(15)
                .build();

        when(mapper.findInsightDistribution(req)).thenReturn(dto);

        var result = service.getOverallStabilityDistribution(req);

        assertThat(result.getEmpCount()).isEqualTo(100);
        assertThat(result.getProgress40()).isEqualTo(20);
        assertThat(result.getProgress80()).isEqualTo(25);
    }

    @Test
    @DisplayName("전체 안정성 분포 - 조회 결과 없음 (getOverallStabilityDistribution_empty)")
    void getOverallStabilityDistribution_empty() {
        var req = new RetentionInsightRequestDto();
        req.setRoundId(3);

        when(mapper.findInsightDistribution(req)).thenReturn(null);

        assertThatThrownBy(() -> service.getOverallStabilityDistribution(req))
                .isInstanceOf(ProspectException.class)
                .hasMessageContaining(ErrorCode.RETENTION_FORECAST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("전체 안정성 분포 - roundId 미지정 예외 (getOverallStabilityDistribution_missingRoundId)")
    void getOverallStabilityDistribution_missingRoundId() {
        var req = new RetentionInsightRequestDto(); // roundId = null

        assertThatThrownBy(() -> service.getOverallStabilityDistribution(req))
                .isInstanceOf(ProspectException.class)
                .hasMessageContaining(ErrorCode.INVALID_REQUEST.getMessage());
    }

    @Test
    @DisplayName("부서별 안정성 분포 - 정상 조회 (getStabilityDistributionByDept_success)")
    void getStabilityDistributionByDept_success() {
        var req = new RetentionInsightRequestDto();
        req.setRoundId(3);

        var dto = StabilityDistributionByDeptDto.builder()
                .deptName("인사팀")
                .positionName("대리")
                .empCount(12)
                .progress20(2)
                .progress40(3)
                .progress60(4)
                .progress80(2)
                .progress100(1)
                .build();

        when(mapper.findInsightDistributionList(req)).thenReturn(List.of(dto));

        var result = service.getStabilityDistributionByDept(req);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDeptName()).isEqualTo("인사팀");
        assertThat(result.get(0).getPositionName()).isEqualTo("대리");
        assertThat(result.get(0).getProgress60()).isEqualTo(4);
    }

    @Test
    @DisplayName("부서별 안정성 분포 - 조회 결과 없음 (getStabilityDistributionByDept_empty)")
    void getStabilityDistributionByDept_empty() {
        var req = new RetentionInsightRequestDto();
        req.setRoundId(3);

        when(mapper.findInsightDistributionList(req)).thenReturn(null); // ← service 내부 로직은 null만 예외 처리함

        assertThatThrownBy(() -> service.getStabilityDistributionByDept(req))
                .isInstanceOf(ProspectException.class)
                .hasMessageContaining(ErrorCode.RETENTION_FORECAST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("근속 지수 시계열 - 정상 조회 (getMonthlyRetentionStats_success)")
    void getMonthlyRetentionStats_success() {
        var req = new RetentionTimeseriesRequestDto();
        req.setYear(2025);

        var stat1 = RetentionMonthlyStatDto.builder()
                .month(1)
                .averageScore(74.1)
                .stdDeviation(9.1)
                .build();

        when(mapper.findMonthlyRetentionStats(req)).thenReturn(List.of(stat1));

        var result = service.getMonthlyRetentionStats(req);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAverageScore()).isEqualTo(74.1);
    }

    @Test
    @DisplayName("근속 지수 시계열 - 연도 미입력 시 현재 연도 자동 적용 (getMonthlyRetentionStats_defaultYear)")
    void getMonthlyRetentionStats_defaultYear() {
        var req = new RetentionTimeseriesRequestDto(); // year = null
        var currentYear = LocalDate.now().getYear();

        var dto = RetentionMonthlyStatDto.builder()
                .month(1)
                .averageScore(80.0)
                .stdDeviation(5.2)
                .build();

        when(mapper.findMonthlyRetentionStats(any(RetentionTimeseriesRequestDto.class)))
                .thenReturn(List.of(dto));

        var result = service.getMonthlyRetentionStats(req);

        assertThat(req.getYear()).isEqualTo(currentYear); // 서비스 내에서 세팅됨
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAverageScore()).isEqualTo(80.0);
    }
}
