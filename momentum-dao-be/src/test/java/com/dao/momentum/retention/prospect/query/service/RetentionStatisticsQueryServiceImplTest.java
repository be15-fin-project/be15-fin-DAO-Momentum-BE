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
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("평균 근속 지수 조회")
    class GetAverageScoreTests {

        @Test
        @DisplayName("정상 조회")
        void getAverageScore_success() {
            var req = RetentionStatisticsRequestDto.builder()
                    .roundId(1)
                    .deptId(10)
                    .positionId(5);

            var dto = RetentionAverageScoreDto.builder()
                    .averageScore(75.5)
                    .totalEmpCount(20)
                    .stabilitySafeRatio(40.0)
                    .stabilityRiskRatio(10.0)
                    .build();

            when(mapper.findAverageRetentionScore(req.build())).thenReturn(dto);

            var result = service.getAverageScore(req.build());

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

            assertThat(result.empCount()).isEqualTo(100);
            assertThat(result.progress40()).isEqualTo(20);
            assertThat(result.progress80()).isEqualTo(25);
        }

        @Test
        @DisplayName("조회 결과 없음")
        void getOverallStabilityDistribution_empty() {
            var req = RetentionInsightRequestDto.builder()
                    .roundId(3)
                    .build();

            when(mapper.findInsightDistribution(req)).thenReturn(null);

            assertThatThrownBy(() -> service.getOverallStabilityDistribution(req))
                    .isInstanceOf(ProspectException.class)
                    .hasMessageContaining(ErrorCode.RETENTION_FORECAST_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("roundId 미지정 예외")
        void getOverallStabilityDistribution_missingRoundId() {
            var req = RetentionInsightRequestDto.builder()
                    .roundId(null)
                    .build(); // roundId = null

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
            assertThat(result.get(0).deptName()).isEqualTo("인사팀");
            assertThat(result.get(0).positionName()).isEqualTo("대리");
            assertThat(result.get(0).progress60()).isEqualTo(4);
        }

        @Test
        @DisplayName("조회 결과 없음")
        void getStabilityDistributionByDept_empty() {
            var req = RetentionInsightRequestDto.builder()
                    .roundId(3)
                    .build();

            when(mapper.findInsightDistributionList(req)).thenReturn(null); // ← service 내부 로직은 null만 예외 처리함

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
                    .month(1)
                    .averageScore(74.1)
                    .stdDeviation(9.1)
                    .build();

            when(mapper.findMonthlyRetentionStats(req)).thenReturn(List.of(stat1));

            var result = service.getMonthlyRetentionStats(req);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).averageScore()).isEqualTo(74.1);
        }

        @Test
        @DisplayName("연도 미입력 시 현재 연도 자동 적용")
        void getMonthlyRetentionStats_defaultYear() {
            var req = RetentionTimeseriesRequestDto.builder()
                    .year(null) // year = null
                    .build();
            var currentYear = LocalDate.now().getYear();

            // mock된 RetentionMonthlyStatDto 생성
            var dto = RetentionMonthlyStatDto.builder()
                    .month(1)
                    .averageScore(80.0)
                    .stdDeviation(5.2)
                    .build();

            // mock이 올바른 데이터를 반환하도록 설정
            when(mapper.findMonthlyRetentionStats(any(RetentionTimeseriesRequestDto.class)))
                    .thenReturn(List.of(dto));  // 유효한 아이템이 들어 있는 리스트 반환

            // 서비스 메서드 호출
            var result = service.getMonthlyRetentionStats(req);

            // 서비스 내에서 year 값이 현재 연도로 설정되었는지 확인
            assertThat(req.year()).isEqualTo(currentYear); // year 값이 현재 연도로 자동 설정됨

            // 반환된 결과가 null이 아니고 예상된 데이터를 포함하고 있는지 확인
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).averageScore()).isEqualTo(80.0);  // 반환된 데이터가 예상과 일치하는지 확인
        }

    }
}
