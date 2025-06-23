package com.dao.momentum.retention.query.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.retention.exception.RetentionException;
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
    @DisplayName("getStabilityDistributionByDept - 정상 조회")
    void getStabilityDistributionByDept_success() {
        // given
        RetentionStatisticsRequestDto req = new RetentionStatisticsRequestDto();
        req.setYear(2025);

        StabilityRatioByDeptRaw raw = new StabilityRatioByDeptRaw();
        raw.setDeptName("인사팀");
        raw.setStableCount(5L);
        raw.setWarningCount(3L);
        raw.setUnstableCount(2L);
        raw.setTotalCount(10L);

        when(mapper.findStabilityDistributionByDept(2025)).thenReturn(List.of(raw));

        // when
        List<StabilityDistributionByDeptDto> result = service.getStabilityDistributionByDept(req);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDeptName()).isEqualTo("인사팀");
        assertThat(result.get(0).getStableRatio()).isEqualTo(50.0);
        assertThat(result.get(0).getWarningRatio()).isEqualTo(30.0);
        assertThat(result.get(0).getUnstableRatio()).isEqualTo(20.0);
    }

    @Test
    @DisplayName("getStabilityDistributionByDept - 결과 없음")
    void getStabilityDistributionByDept_null() {
        // given
        RetentionStatisticsRequestDto req = new RetentionStatisticsRequestDto();
        req.setYear(2025);

        when(mapper.findStabilityDistributionByDept(2025)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> service.getStabilityDistributionByDept(req))
                .isInstanceOf(RetentionException.class)
                .hasMessageContaining(ErrorCode.RETENTION_FORECAST_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("getOverallStabilityDistribution - 전체 조회 성공")
    void getOverallStabilityDistribution_success() {
        // given
        RetentionStatisticsRequestDto req = new RetentionStatisticsRequestDto();
        req.setYear(2025);
        req.setDeptId(null); // 전체 조회

        StabilityRatioByDeptRaw raw = new StabilityRatioByDeptRaw();
        raw.setDeptName(null);
        raw.setStableCount(4L);
        raw.setWarningCount(4L);
        raw.setUnstableCount(2L);
        raw.setTotalCount(10L);

        when(mapper.findOverallStabilityDistribution(req)).thenReturn(raw);

        // when
        var result = service.getOverallStabilityDistribution(req);

        // then
        assertThat(result.getDeptName()).isEqualTo("전체");
        assertThat(result.getStableRatio()).isEqualTo(40.0);
        assertThat(result.getWarningRatio()).isEqualTo(40.0);
        assertThat(result.getUnstableRatio()).isEqualTo(20.0);
    }
}
