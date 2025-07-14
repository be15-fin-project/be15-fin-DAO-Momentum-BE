package com.dao.momentum.work.command.application.service;

import com.dao.momentum.approve.command.domain.aggregate.Approve;
import com.dao.momentum.approve.command.domain.repository.ApproveRepository;
import com.dao.momentum.work.command.application.validator.WorkCreateValidator;
import com.dao.momentum.work.command.domain.aggregate.Overtime;
import com.dao.momentum.work.command.domain.aggregate.RemoteWork;
import com.dao.momentum.work.command.domain.repository.OvertimeRepository;
import com.dao.momentum.work.command.domain.repository.RemoteWorkRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WlbRetentionServiceTest {

    @InjectMocks
    private WlbRetentionService wlbRetentionService;

    @Mock
    private OvertimeRepository overtimeRepository;

    @Mock
    private RemoteWorkRepository remoteWorkRepository;

    @Mock
    private ApproveRepository approveRepository;

    @Mock
    private WorkCreateValidator workCreateValidator;

    private final Long empId = 100L;

    @Test
    @DisplayName("특정 사원의 초과 근무 점수를 계산하는 테스트")
    void getOvertimeScore() {
        int targetYear = 2025;
        int targetMonth = 7;

        LocalDate firstOfMonth = LocalDate.of(targetYear, targetMonth, 1);
        LocalDate currentMonthWeekStart = firstOfMonth.with(DayOfWeek.MONDAY);
        LocalDate endDate = currentMonthWeekStart.minusDays(1);
        LocalDate startDate = endDate.minusWeeks(4).with(DayOfWeek.MONDAY);

        LocalDateTime startAt = LocalDateTime.of(startDate.plusDays(2), LocalTime.of(18, 0));
        LocalDateTime endAt = startAt.plusHours(4).plusMinutes(30);

        Overtime overtime = Overtime.builder()
                .approveId(1L)
                .startAt(startAt)
                .endAt(endAt)
                .build();

        Approve approve = Approve.builder()
                .empId(empId)
                .build();
        approve.updateApproveStatus(2); // 승인 상태로 설정

        when(overtimeRepository.findOvertimesBetween(startDate, endDate)).thenReturn(List.of(overtime));
        when(approveRepository.getApproveByApproveId(1L)).thenReturn(Optional.of(approve));

        int result = wlbRetentionService.getOvertimeScore(empId, targetYear, targetMonth);

        assertThat(result).isEqualTo(0); // 4시간 30분 = 270분 → 감점 없음
    }

    @Test
    @DisplayName("특정 사원의 재택 근무 점수를 계산하는 테스트")
    void getRemoteWorkScore() {
        int targetYear = 2025;
        int targetMonth = 7;

        LocalDate firstOfMonth = LocalDate.of(targetYear, targetMonth, 1);
        LocalDate currentMonthWeekStart = firstOfMonth.with(DayOfWeek.MONDAY);
        LocalDate endDate = currentMonthWeekStart.minusDays(1);
        LocalDate startDate = endDate.minusWeeks(4).with(DayOfWeek.MONDAY);

        LocalDate remoteStart = startDate.plusDays(1);
        LocalDate remoteEnd = remoteStart.plusDays(2);

        RemoteWork remoteWork = RemoteWork.builder()
                .approveId(2L)
                .startDate(remoteStart)
                .endDate(remoteEnd)
                .build();

        Approve approve = Approve.builder()
                .empId(empId)
                .build();
        approve.updateApproveStatus(2);

        when(remoteWorkRepository.findRemoteWorksBetween(startDate, endDate)).thenReturn(List.of(remoteWork));
        when(approveRepository.getApproveByApproveId(2L)).thenReturn(Optional.of(approve));
        when(workCreateValidator.isHoliday(any())).thenReturn(false); // 모든 날을 평일로 가정

        int result = wlbRetentionService.getRemoteWorkScore(empId, targetYear, targetMonth);

        assertThat(result).isEqualTo(0); // 총 근무일 중 15% 이하일 경우 점수 0
    }
}