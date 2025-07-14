package com.dao.momentum.work.command.application.service;

import com.dao.momentum.approve.command.domain.aggregate.Approve;
import com.dao.momentum.approve.command.domain.repository.ApproveRepository;
import com.dao.momentum.approve.exception.ApproveException;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.work.command.application.validator.WorkCreateValidator;
import com.dao.momentum.work.command.domain.aggregate.Overtime;
import com.dao.momentum.work.command.domain.aggregate.RemoteWork;
import com.dao.momentum.work.command.domain.repository.OvertimeRepository;
import com.dao.momentum.work.command.domain.repository.RemoteWorkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WlbRetentionService {

    private final OvertimeRepository overtimeRepository;
    private final RemoteWorkRepository remoteWorkRepository;
    private final ApproveRepository approveRepository;
    private final WorkCreateValidator workCreateValidator;

    /*
    * 사원별로 최근 4주간 초과시간 근무 관련 계산
    * */
    public int getOvertimeScore(Long empId,int targetYear, int targetMonth) {
        LocalDate firstOfMonth = LocalDate.of(targetYear, targetMonth, 1);

        int totalMinutes = 0;

        // 1. 해당 월의 첫 날이 속한 주의 시작일 (월요일)
        LocalDate currentMonthWeekStart = firstOfMonth.with(DayOfWeek.MONDAY);

        // 2. 그 주의 전날인 일요일 = 평가 대상 endDate
        LocalDate endDate = currentMonthWeekStart.minusDays(1);

        // 4. endDate 기준 4주 전의 월요일 = startDate
        LocalDate startDate = endDate.minusWeeks(4).with(DayOfWeek.MONDAY);

        // 5. 해당 기간 내 Overtime 조회
        List<Overtime> overtimeList = overtimeRepository.findOvertimesBetween(startDate, endDate);

        // 6. Overtime에서 사원의 초과 근무 시간 계산하기
        for (Overtime overtime : overtimeList) {
            Approve approve = approveRepository.getApproveByApproveId(overtime.getApproveId())
                    .orElseThrow(() -> new ApproveException(ErrorCode.NOT_EXIST_APPROVE));

            if (approve.getStatusId() != 2 || !approve.getEmpId().equals(empId)) continue;

            totalMinutes += calculateMinutes(overtime.getStartAt(), overtime.getEndAt());
        }

        return calculateOvertimeScore(totalMinutes);
    }

    /*
     * 사원별로 최근 4주간 재택근무 관련 계산
     * */
    public int getRemoteWorkScore(Long empId,int targetYear, int targetMonth) {
        LocalDate firstOfMonth = LocalDate.of(targetYear, targetMonth, 1);

        // 1. 해당 월의 첫 날이 속한 주의 시작일 (월요일)
        LocalDate currentMonthWeekStart = firstOfMonth.with(DayOfWeek.MONDAY);

        // 2. 그 주의 전날인 일요일 = 평가 대상 endDate
        LocalDate endDate = currentMonthWeekStart.minusDays(1);

        // 4. endDate 기준 4주 전의 월요일 = startDate
        LocalDate startDate = endDate.minusWeeks(4).with(DayOfWeek.MONDAY);

        // 5. 근무일 계산 (휴일 제외)
        Set<LocalDate> workdays = getWorkdaysBetween(startDate, endDate);

        int totalWorkdays = workdays.size();

        // 6. 해당 기간 내 재택근무 내역 조회
        List<RemoteWork> remoteWorkList = remoteWorkRepository.findRemoteWorksBetween(startDate, endDate);

        // 7. 재택 근무 일수 집계
        Set<LocalDate> remoteDays = new HashSet<>();

        for (RemoteWork remoteWork : remoteWorkList) {
            Approve approve = approveRepository.getApproveByApproveId(remoteWork.getApproveId())
                    .orElseThrow(() -> new ApproveException(ErrorCode.NOT_EXIST_APPROVE));

            if (approve.getStatusId() != 2 || !approve.getEmpId().equals(empId)) continue;

            // 근무일로 인정되는 날인지 체크
            for (LocalDate date = remoteWork.getStartDate(); !date.isAfter(remoteWork.getEndDate()); date = date.plusDays(1)) {
                if (workdays.contains(date)) {
                    remoteDays.add(date);
                }
            }
        }

        // 8. 점수 계산
        double ratio = (double) remoteDays.size() / totalWorkdays;

        return ratio > 0.15 ? 2 : 0;
    }


    /*
     * 초과 근무 시간별로 감점 점수로 변환하기
     */
    private int calculateOvertimeScore(int totalMinutes) {
        if (totalMinutes >= 2400) return -10; // 40시간
        if (totalMinutes >= 1920) return -8;  // 32시간
        if (totalMinutes >= 1440) return -6;  // 24시간
        if (totalMinutes >= 960)  return -4;  // 16시간
        if (totalMinutes >= 480)  return -2;  // 8시간
        return 0;
    }


    /*
     * 초과 근무 시작~종료 시각 간 차이 계산하기
     */
    private int calculateMinutes(LocalDateTime start, LocalDateTime end) {
        return (int) Duration.between(start, end).toMinutes();
    }


    /*
    * 근무 날짜 계산하기 (공휴일 제외하기)
    * */
    private Set<LocalDate> getWorkdaysBetween(LocalDate start, LocalDate end) {
        Set<LocalDate> workdays = new HashSet<>();

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            if (!workCreateValidator.isHoliday(date)) {
                workdays.add(date);
            }
        }

        return workdays;
    }

}
