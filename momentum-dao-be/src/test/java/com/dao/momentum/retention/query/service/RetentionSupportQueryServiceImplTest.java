package com.dao.momentum.retention.query.service;

import com.dao.momentum.common.dto.Pagination;
import com.dao.momentum.retention.query.dto.request.RetentionSupportRaw;
import com.dao.momentum.retention.query.dto.request.RetentionForecastRequestDto;
import com.dao.momentum.retention.query.dto.response.RetentionForecastResponseDto;
import com.dao.momentum.retention.query.mapper.RetentionSupportMapper;
import com.dao.momentum.retention.command.domain.aggregate.StabilityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RetentionSupportQueryServiceImplTest {

    @Mock
    private RetentionSupportMapper mapper;

    @InjectMocks
    private RetentionSupportQueryServiceImpl service;

    @Test
    @DisplayName("근속 전망 목록 정상 조회")
    void getRetentionForecasts_success() {
        // given
        RetentionForecastRequestDto req = new RetentionForecastRequestDto();
        req.setPage(1);
        req.setSize(10);
        req.setRoundNo(3);

        RetentionSupportRaw raw = new RetentionSupportRaw();
        raw.setEmpName("김예진");
        raw.setDeptName("인사팀");
        raw.setPositionName("과장");
        raw.setRetentionScore(75); // → "우수", "주의형"
        raw.setSummaryComment("직무:우수, 관계:보통");
        raw.setRoundNo(3);

        when(mapper.findRetentionForecasts(req, 3)).thenReturn(List.of(raw));
        when(mapper.countRetentionForecasts(req, 3)).thenReturn(1L);

        // when
        RetentionForecastResponseDto result = service.getRetentionForecasts(req);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getRetentionGrade()).isEqualTo("우수");
        assertThat(result.getItems().get(0).getStabilityType()).isEqualTo(StabilityType.WARNING);
        assertThat(result.getPagination().getTotalItems()).isEqualTo(1L);

        verify(mapper).findRetentionForecasts(req, 3);
        verify(mapper).countRetentionForecasts(req, 3);
    }

    @Test
    @DisplayName("근속 전망 필터 적용 - 안정성 유형에 맞는 데이터만 반환")
    void getRetentionForecasts_withStabilityTypeFilter() {
        // given
        RetentionForecastRequestDto req = new RetentionForecastRequestDto();
        req.setPage(1);
        req.setSize(10);
        req.setRoundNo(3);
        req.setStabilityType(StabilityType.STABLE);

        RetentionSupportRaw raw = new RetentionSupportRaw();
        raw.setEmpName("김예진");
        raw.setDeptName("인사팀");
        raw.setPositionName("과장");
        raw.setRetentionScore(75); // WARNING → 필터 대상 아님
        raw.setSummaryComment("직무:우수, 관계:보통");
        raw.setRoundNo(3);

        when(mapper.findRetentionForecasts(req, 3)).thenReturn(List.of(raw));
        when(mapper.countRetentionForecasts(req, 3)).thenReturn(1L);

        // when
        RetentionForecastResponseDto result = service.getRetentionForecasts(req);

        // then
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getPagination().getTotalItems()).isEqualTo(1L);
    }

    @Test
    @DisplayName("roundNo가 null이면 최신 회차 사용")
    void getRetentionForecasts_roundNoNull_defaultsToLatest() {
        // given
        RetentionForecastRequestDto req = new RetentionForecastRequestDto();
        req.setPage(1);
        req.setSize(10);
        req.setRoundNo(null); // 생략 → 최신 회차로 대체

        RetentionSupportRaw raw = new RetentionSupportRaw();
        raw.setEmpName("김예진");
        raw.setDeptName("인사팀");
        raw.setPositionName("과장");
        raw.setRetentionScore(90); // → "탁월", "안정형"
        raw.setSummaryComment("직무:탁월, 관계:탁월");
        raw.setRoundNo(5);

        when(mapper.findLatestRoundNo()).thenReturn(5);
        when(mapper.findRetentionForecasts(req, 5)).thenReturn(List.of(raw));
        when(mapper.countRetentionForecasts(req, 5)).thenReturn(1L);

        // when
        RetentionForecastResponseDto result = service.getRetentionForecasts(req);

        // then
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getRetentionGrade()).isEqualTo("탁월");
        assertThat(result.getItems().get(0).getStabilityType()).isEqualTo(StabilityType.STABLE);
    }

    @Test
    @DisplayName("조회 결과가 빈 경우에도 예외 없이 빈 리스트 반환")
    void getRetentionForecasts_emptyList_safeReturn() {
        // given
        RetentionForecastRequestDto req = new RetentionForecastRequestDto();
        req.setPage(1);
        req.setSize(10);
        req.setRoundNo(3);

        when(mapper.findRetentionForecasts(req, 3)).thenReturn(Collections.emptyList());
        when(mapper.countRetentionForecasts(req, 3)).thenReturn(0L);

        // when
        RetentionForecastResponseDto result = service.getRetentionForecasts(req);

        // then
        assertThat(result.getItems()).isEmpty();
        assertThat(result.getPagination().getTotalItems()).isZero();
    }
}
