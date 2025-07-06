package com.dao.momentum.retention.prospect.query.service;

import com.dao.momentum.retention.prospect.query.dto.request.RetentionRoundRawDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionRoundSearchRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionRoundListResultDto;
import com.dao.momentum.retention.prospect.query.mapper.RetentionRoundMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class RetentionRoundQueryServiceImplTest {

    @Mock
    private RetentionRoundMapper mapper;

    @InjectMocks
    private RetentionRoundQueryServiceImpl service;

    private RetentionRoundSearchRequestDto req;

    @BeforeEach
    void setUp() {
        req = RetentionRoundSearchRequestDto.builder()
                .page(1)
                .size(10)
                .build();
    }

    @Nested
    @DisplayName("근속 회차 조회")
    class GetRetentionRoundsTests {

        @Test
        @DisplayName("정상 조회")
        void getRetentionRounds_success() {
            LocalDate start = LocalDate.of(2025, 6, 1);
            LocalDate end = LocalDate.of(2025, 6, 30);

            RetentionRoundRawDto raw = RetentionRoundRawDto.builder()
                    .roundId(1)
                    .roundNo(5)
                    .year(2025)
                    .month(6)
                    .startDate(start)
                    .endDate(end)
                    .participantCount(45)
                    .build();

            when(mapper.findRetentionRounds(req)).thenReturn(List.of(raw));
            when(mapper.countRetentionRounds(req)).thenReturn(1L);

            RetentionRoundListResultDto result = service.getRetentionRounds(req);

            assertThat(result.content()).hasSize(1);
            assertThat(result.content().get(0).roundId()).isEqualTo(1);
            assertThat(result.content().get(0).roundNo()).isEqualTo(5);
            assertThat(result.pagination().getTotalItems()).isEqualTo(1);
        }

        @Test
        @DisplayName("조회 결과 없음")
        void getRetentionRounds_empty() {
            when(mapper.findRetentionRounds(req)).thenReturn(List.of());
            when(mapper.countRetentionRounds(req)).thenReturn(0L);

            RetentionRoundListResultDto result = service.getRetentionRounds(req);

            assertThat(result.content()).isEmpty();
            assertThat(result.pagination().getTotalItems()).isEqualTo(0L);
            assertThat(result.pagination().getTotalPage()).isEqualTo(0);
        }
    }
}
