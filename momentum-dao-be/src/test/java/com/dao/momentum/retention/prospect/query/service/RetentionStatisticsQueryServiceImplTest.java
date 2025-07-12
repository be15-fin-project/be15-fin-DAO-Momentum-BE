package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.retention.prospect.exception.ProspectException;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionInsightRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionStatisticsRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionTimeseriesRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionAverageScoreDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionMonthlyStatDto;
import com.dao.momentum.retention.prospect.query.dto.response.StabilityRatioByDeptDto;
import com.dao.momentum.retention.prospect.query.dto.response.StabilityRatioSummaryDto;
import com.dao.momentum.retention.prospect.query.mapper.RetentionStatisticsMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetentionStatisticsQueryServiceImplTest {

    @Mock
    private RetentionStatisticsMapper mapper;

    @InjectMocks
    private RetentionStatisticsQueryServiceImpl service;

    @Nested
    @DisplayName("평균 근속 지수 조회")
    class GetAverageScoreTests {

        @Test
        @DisplayName("정상 조회")
        void getAverageScore_success() {
            var req = RetentionStatisticsRequestDto.builder()
                    .roundId(1)
                    .deptId(10)
                    .positionId(5)
                    .build();

            var dto = RetentionAverageScoreDto.builder()
                    .averageScore(75.5)
                    .totalEmpCount(20L)
                    .stabilitySafeRatio(40.0)
                    .stabilityRiskRatio(10.0)
                    .build();

            when(mapper.findAverageRetentionScore(req)).thenReturn(dto);

            var result = service.getAverageScore(req);

            assertThat(result.averageScore()).isEqualTo(75.5);
            assertThat(result.totalEmpCount()).isEqualTo(20);
            assertThat(result.stabilitySafeRatio()).isEqualTo(40.0);
            assertThat(result.stabilityRiskRatio()).isEqualTo(10.0);
        }

        @Test
        @DisplayName("조회 결과 없음")
        void getAverageScore_empty() {
            var req = RetentionStatisticsRequestDto.builder()
                    .roundId(1)
                    .build();

            when(mapper.findAverageRetentionScore(req)).thenReturn(null);

            assertThatThrownBy(() -> service.getAverageScore(req))
                    .isInstanceOf(ProspectException.class)
                    .hasMessageContaining(ErrorCode.RETENTION_FORECAST_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("전체 안정성 분포 조회")
    class GetOverallStabilityDistributionTests {

        @Test
        @DisplayName("정상 조회")
        void getOverallStabilityDistribution_success() {
            var req = RetentionInsightRequestDto.builder()
                    .roundId(3)
                    .build();

            var dto = StabilityRatioSummaryDto.builder()
                    .goodCount(100L)
                    .normalCount(30L)
                    .warningCount(15L)
                    .severeCount(9L)
                    .totalCount(154L)
                    .build();

            when(mapper.findOverallStabilityRatio(req)).thenReturn(dto);

            var result = service.getOverallStabilityDistribution(req);

            assertThat(result.goodCount()).isEqualTo(100L);
            assertThat(result.normalCount()).isEqualTo(30L);
            assertThat(result.warningCount()).isEqualTo(15L);
            assertThat(result.severeCount()).isEqualTo(9L);
            assertThat(result.totalCount()).isEqualTo(154L);
        }

        @Test
        @DisplayName("조회 결과 없음")
        void getOverallStabilityDistribution_empty() {
            var req = RetentionInsightRequestDto.builder()
                    .roundId(3)
                    .build();

            when(mapper.findOverallStabilityRatio(req)).thenReturn(null);

            assertThatThrownBy(() -> service.getOverallStabilityDistribution(req))
                    .isInstanceOf(ProspectException.class)
                    .hasMessageContaining(ErrorCode.RETENTION_FORECAST_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("roundId 미지정 예외")
        void getOverallStabilityDistribution_missingRoundId() {
            var req = RetentionInsightRequestDto.builder()
                    .roundId(null)
                    .build();

            assertThatThrownBy(() -> service.getOverallStabilityDistribution(req))
                    .isInstanceOf(ProspectException.class)
                    .hasMessageContaining(ErrorCode.INVALID_REQUEST.getMessage());
        }
    }

    @Nested
    @DisplayName("부서별 안정성 분포 조회")
    class GetStabilityDistributionByDeptTests {

        @Test
        @DisplayName("정상 조회")
        void getStabilityDistributionByDept_success() {
            var req = RetentionInsightRequestDto.builder()
                    .roundId(3)
                    .build();

            var dto = StabilityRatioByDeptDto.builder()
                    .deptId(1)
                    .deptName("인사팀")
                    .positionId(2)
                    .positionName("대리")
                    .empCount(12)
                    .progress20(2)
                    .progress40(3)
                    .progress60(4)
                    .progress80(2)
                    .progress100(1)
                    .build();

            when(mapper.findProgressDistributionByDept(req)).thenReturn(List.of(dto));

            var result = service.getStabilityDistributionByDept(req);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).deptName()).isEqualTo("인사팀");
            assertThat(result.get(0).progress60()).isEqualTo(4);
        }

        @Test
        @DisplayName("조회 결과 없음")
        void getStabilityDistributionByDept_empty() {
            var req = RetentionInsightRequestDto.builder()
                    .roundId(3)
                    .build();

            when(mapper.findProgressDistributionByDept(req)).thenReturn(null);

            assertThatThrownBy(() -> service.getStabilityDistributionByDept(req))
                    .isInstanceOf(ProspectException.class)
                    .hasMessageContaining(ErrorCode.RETENTION_FORECAST_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("근속 지수 시계열 조회")
    class GetMonthlyRetentionStatsTests {

        @Test
        @DisplayName("정상 조회")
        void getMonthlyRetentionStats_success() {
            var req = RetentionTimeseriesRequestDto.builder()
                    .year(2025)
                    .build();

            var stat1 = RetentionMonthlyStatDto.builder()
                    .year(2025)
                    .month(1)
                    .averageScore(74.1)
                    .stdDeviation(9.1)
                    .build();

            when(mapper.findMonthlyRetentionStats(req)).thenReturn(List.of(stat1));

            var result = service.getMonthlyRetentionStats(req);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).averageScore()).isEqualTo(74.1);
            assertThat(result.get(0).month()).isEqualTo(1);
        }

        @Test
        @DisplayName("연도 누락 시 현재 연도 자동 보정")
        void getMonthlyRetentionStats_defaultYear() {
            var stat1 = RetentionMonthlyStatDto.builder()
                    .year(2025)
                    .month(6)
                    .averageScore(80.0)
                    .stdDeviation(8.2)
                    .build();

            when(mapper.findMonthlyRetentionStats(any())).thenReturn(List.of(stat1));

            var result = service.getMonthlyRetentionStats(RetentionTimeseriesRequestDto.builder().year(null).build());

            assertThat(result).hasSize(1);
            assertThat(result.get(0).averageScore()).isEqualTo(80.0);
        }
    }
}
