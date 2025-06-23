package com.dao.momentum.retention.query.service;

import com.dao.momentum.retention.query.dto.request.RetentionRoundRawDto;
import com.dao.momentum.retention.query.dto.request.RetentionRoundSearchRequestDto;
import com.dao.momentum.retention.query.dto.response.RetentionRoundListResultDto;
import com.dao.momentum.retention.query.mapper.RetentionRoundMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
        req = new RetentionRoundSearchRequestDto();
        req.setPage(1);
        req.setSize(10);
    }

    @Test
    @DisplayName("getRetentionRounds - 정상 조회")
    void getRetentionRounds_success() {
        LocalDate start = LocalDate.of(2025, 6, 1);
        LocalDate end = LocalDate.of(2025, 6, 30);

        RetentionRoundRawDto raw = new RetentionRoundRawDto();
        raw.setRoundId(1);
        raw.setRoundNo(5);
        raw.setYear(2025);
        raw.setMonth(6);
        raw.setStartDate(start);
        raw.setEndDate(end);
        raw.setParticipantCount(45);

        when(mapper.findRetentionRounds(req)).thenReturn(List.of(raw));
        when(mapper.countRetentionRounds(req)).thenReturn(1L);

        RetentionRoundListResultDto result = service.getRetentionRounds(req);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRoundId()).isEqualTo(1);
        assertThat(result.getContent().get(0).getRoundNo()).isEqualTo(5);
        assertThat(result.getPagination().getTotalItems()).isEqualTo(1);
    }

    @Test
    @DisplayName("getRetentionRounds - 회차 상태: 예정")
    void getRetentionRounds_status_planned() {
        LocalDate start = LocalDate.now().plusDays(10);
        LocalDate end = start.plusDays(5);

        RetentionRoundRawDto raw = new RetentionRoundRawDto();
        raw.setRoundId(2);
        raw.setRoundNo(6);
        raw.setYear(2025);
        raw.setMonth(7);
        raw.setStartDate(start);
        raw.setEndDate(end);
        raw.setParticipantCount(10);

        when(mapper.findRetentionRounds(req)).thenReturn(List.of(raw));
        when(mapper.countRetentionRounds(req)).thenReturn(1L);

        String status = service.getRetentionRounds(req).getContent().get(0).getStatus();
        assertThat(status).isEqualTo("예정");
    }

    @Test
    @DisplayName("getRetentionRounds - 회차 상태: 진행 중")
    void getRetentionRounds_status_inProgress() {
        LocalDate start = LocalDate.now().minusDays(1);
        LocalDate end = LocalDate.now().plusDays(1);

        RetentionRoundRawDto raw = new RetentionRoundRawDto();
        raw.setRoundId(3);
        raw.setRoundNo(7);
        raw.setYear(2025);
        raw.setMonth(8);
        raw.setStartDate(start);
        raw.setEndDate(end);
        raw.setParticipantCount(20);

        when(mapper.findRetentionRounds(req)).thenReturn(List.of(raw));
        when(mapper.countRetentionRounds(req)).thenReturn(1L);

        String status = service.getRetentionRounds(req).getContent().get(0).getStatus();
        assertThat(status).isEqualTo("진행 중");
    }

    @Test
    @DisplayName("getRetentionRounds - 회차 상태: 완료")
    void getRetentionRounds_status_completed() {
        LocalDate start = LocalDate.now().minusDays(10);
        LocalDate end = LocalDate.now().minusDays(1);

        RetentionRoundRawDto raw = new RetentionRoundRawDto();
        raw.setRoundId(4);
        raw.setRoundNo(8);
        raw.setYear(2025);
        raw.setMonth(9);
        raw.setStartDate(start);
        raw.setEndDate(end);
        raw.setParticipantCount(30);

        when(mapper.findRetentionRounds(req)).thenReturn(List.of(raw));
        when(mapper.countRetentionRounds(req)).thenReturn(1L);

        String status = service.getRetentionRounds(req).getContent().get(0).getStatus();
        assertThat(status).isEqualTo("완료");
    }

    @Test
    @DisplayName("getRetentionRounds - 조회 결과 없음")
    void getRetentionRounds_empty() {
        when(mapper.findRetentionRounds(req)).thenReturn(List.of());
        when(mapper.countRetentionRounds(req)).thenReturn(0L);

        RetentionRoundListResultDto result = service.getRetentionRounds(req);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.getPagination().getTotalItems()).isEqualTo(0L);
        assertThat(result.getPagination().getTotalPage()).isEqualTo(0);
    }
}
