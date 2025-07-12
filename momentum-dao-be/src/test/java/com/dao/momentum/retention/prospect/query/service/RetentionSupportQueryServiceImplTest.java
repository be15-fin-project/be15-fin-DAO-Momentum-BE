package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.retention.prospect.query.dto.request.RetentionSupportRaw;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionForecastRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionForecastResponseDto;
import com.dao.momentum.retention.prospect.query.mapper.RetentionSupportMapper;
import com.dao.momentum.retention.prospect.command.domain.aggregate.StabilityType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

    @Nested
    @DisplayName("근속 전망 조회")
    class GetRetentionForecastsTests {

        @Test
        @DisplayName("정상 조회")
        void getRetentionForecasts_success() {
            // given
            RetentionForecastRequestDto req = RetentionForecastRequestDto.builder()
                    .page(1)
                    .size(10)
                    .roundNo(3)
                    .build();

            RetentionSupportRaw raw = RetentionSupportRaw.builder()
                    .empName("김예진")
                    .deptName("인사팀")
                    .positionName("과장")
                    .retentionScore(75)
                    .summaryComment("직무:우수, 관계:보통")
                    .roundNo(3)
                    .build();

            when(mapper.findRetentionForecasts(req, 3)).thenReturn(List.of(raw));
            when(mapper.countRetentionForecasts(req, 3)).thenReturn(1L);

            // when
            RetentionForecastResponseDto result = service.getRetentionForecasts(req);

            // then
            assertThat(result).isNotNull();
            assertThat(result.items()).hasSize(1);
            assertThat(result.items().get(0).retentionGrade()).isEqualTo("우수");
            assertThat(result.items().get(0).stabilityType()).isEqualTo(StabilityType.WARNING);
            assertThat(result.pagination().getTotalItems()).isEqualTo(1L);

            verify(mapper).findRetentionForecasts(req, 3);
            verify(mapper).countRetentionForecasts(req, 3);
        }

        @Test
        @DisplayName("안정성 유형 필터 적용")
        void getRetentionForecasts_withStabilityTypeFilter() {
            // given
            RetentionForecastRequestDto req = RetentionForecastRequestDto.builder()
                    .page(1)
                    .size(10)
                    .roundNo(3)
                    .stabilityType(StabilityType.GOOD)
                    .build();

            RetentionSupportRaw raw = RetentionSupportRaw.builder()
                    .empName("김예진")
                    .deptName("인사팀")
                    .positionName("과장")
                    .retentionScore(75)
                    .summaryComment("직무:우수, 관계:보통")
                    .roundNo(3)
                    .build();

            when(mapper.findRetentionForecasts(req, 3)).thenReturn(List.of(raw));
            when(mapper.countRetentionForecasts(req, 3)).thenReturn(1L);

            // when
            RetentionForecastResponseDto result = service.getRetentionForecasts(req);

            // then
            assertThat(result.items()).isEmpty();
            assertThat(result.pagination().getTotalItems()).isEqualTo(1L);
        }

        @Test
        @DisplayName("roundNo가 null이면 최신 회차 사용")
        void getRetentionForecasts_roundNoNull_defaultsToLatest() {
            // given
            RetentionForecastRequestDto req = RetentionForecastRequestDto.builder()
                    .page(1)
                    .size(10)
                    .roundNo(null)
                    .build();

            RetentionSupportRaw raw = RetentionSupportRaw.builder()
                    .empName("김예진")
                    .deptName("인사팀")
                    .positionName("과장")
                    .retentionScore(90)
                    .summaryComment("직무:탁월, 관계:탁월")
                    .roundNo(5)
                    .build();

            when(mapper.findLatestRoundNo()).thenReturn(5);
            when(mapper.findRetentionForecasts(req, 5)).thenReturn(List.of(raw));
            when(mapper.countRetentionForecasts(req, 5)).thenReturn(1L);

            // when
            RetentionForecastResponseDto result = service.getRetentionForecasts(req);

            // then
            assertThat(result.items()).hasSize(1);
            assertThat(result.items().get(0).retentionGrade()).isEqualTo("탁월");
            assertThat(result.items().get(0).stabilityType()).isEqualTo(StabilityType.GOOD);
        }

        @Test
        @DisplayName("조회 결과가 빈 경우에도 예외 없이 빈 리스트 반환")
        void getRetentionForecasts_emptyList_safeReturn() {
            // given
            RetentionForecastRequestDto req = RetentionForecastRequestDto.builder()
                    .page(1)
                    .size(10)
                    .roundNo(3)
                    .build();

            when(mapper.findRetentionForecasts(req, 3)).thenReturn(Collections.emptyList());
            when(mapper.countRetentionForecasts(req, 3)).thenReturn(0L);

            // when
            RetentionForecastResponseDto result = service.getRetentionForecasts(req);

            // then
            assertThat(result.items()).isEmpty();
            assertThat(result.pagination().getTotalItems()).isZero();
        }
    }
}
