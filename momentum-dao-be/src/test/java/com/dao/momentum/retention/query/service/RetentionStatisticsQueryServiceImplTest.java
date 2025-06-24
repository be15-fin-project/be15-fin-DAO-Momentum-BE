package com.dao.momentum.retention.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.retention.exception.RetentionException;
import com.dao.momentum.retention.query.dto.request.RetentionInsightRequestDto;
import com.dao.momentum.retention.query.dto.request.RetentionStatisticsRequestDto;
import com.dao.momentum.retention.query.dto.request.StabilityRatioByDeptRaw;
import com.dao.momentum.retention.query.dto.response.RetentionAverageScoreDto;
import com.dao.momentum.retention.query.dto.response.StabilityDistributionByDeptDto;
import com.dao.momentum.retention.query.mapper.RetentionStatisticsMapper;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("getAverageScore - 정상 조회")
    void getAverageScore_success() {
        // given
        RetentionStatisticsRequestDto req = new RetentionStatisticsRequestDto();
        req.setYear(2025);
        req.setMonth(6);
        req.setDeptId(1);

        when(mapper.findAverageRetentionScore(req))
                .thenReturn(RetentionAverageScoreDto.builder().averageScore(75.5).build());

        // when
        RetentionAverageScoreDto result = service.getAverageScore(req);

        // then
        assertThat(result.getAverageScore()).isEqualTo(75.5);
    }

    @Test
    @DisplayName("getAverageScore - 조회 결과 없음")
    void getAverageScore_empty() {
        // given
        RetentionStatisticsRequestDto req = new RetentionStatisticsRequestDto();
        req.setYear(2025);

        when(mapper.findAverageRetentionScore(req)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> service.getAverageScore(req))
                .isInstanceOf(RetentionException.class)
                .hasMessageContaining(ErrorCode.RETENTION_FORECAST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("getOverallStabilityDistribution - 전체 기준 조회 성공")
    void getOverallStabilityDistribution_success() {
        // given
        var req = new RetentionInsightRequestDto();
        req.setRoundId(3); // 필수

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

        // when
        var result = service.getOverallStabilityDistribution(req);

        // then
        assertThat(result.getDeptName()).isEqualTo("전체");
        assertThat(result.getEmpCount()).isEqualTo(100);
        assertThat(result.getProgress80()).isEqualTo(25);
    }

    @Test
    @DisplayName("getOverallStabilityDistribution - 조회 결과 없음")
    void getOverallStabilityDistribution_empty() {
        // given
        var req = new RetentionInsightRequestDto();
        req.setRoundId(3);

        when(mapper.findInsightDistribution(req)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> service.getOverallStabilityDistribution(req))
                .isInstanceOf(RetentionException.class)
                .hasMessageContaining(ErrorCode.RETENTION_FORECAST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("getStabilityDistributionByDept - 정상 조회")
    void getStabilityDistributionByDept_success() {
        // given
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

        // when
        var result = service.getStabilityDistributionByDept(req);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDeptName()).isEqualTo("인사팀");
        assertThat(result.get(0).getProgress60()).isEqualTo(4);
    }

    @Test
    @DisplayName("getStabilityDistributionByDept - 조회 결과 없음")
    void getStabilityDistributionByDept_empty() {
        // given
        var req = new RetentionInsightRequestDto();
        req.setRoundId(3);

        when(mapper.findInsightDistributionList(req)).thenReturn(List.of());

        // when & then
        assertThatThrownBy(() -> service.getStabilityDistributionByDept(req))
                .isInstanceOf(RetentionException.class)
                .hasMessageContaining(ErrorCode.RETENTION_FORECAST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("getOverallStabilityDistribution - roundId 누락 예외")
    void getOverallStabilityDistribution_missingRoundId() {
        // given
        var req = new RetentionInsightRequestDto(); // roundId 미설정

        // when & then
        assertThatThrownBy(() -> service.getOverallStabilityDistribution(req))
                .isInstanceOf(RetentionException.class)
                .hasMessageContaining(ErrorCode.INVALID_REQUEST.getMessage());
    }
}
