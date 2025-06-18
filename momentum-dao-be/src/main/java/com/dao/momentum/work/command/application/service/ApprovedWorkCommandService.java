package com.dao.momentum.work.command.application.service;

import com.dao.momentum.approve.command.domain.aggregate.ApproveType;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.work.command.application.validator.WorkCreateValidator;
import com.dao.momentum.work.command.domain.aggregate.*;
import com.dao.momentum.work.command.domain.repository.WorkRepository;
import com.dao.momentum.work.command.domain.repository.WorkTypeRepository;
import com.dao.momentum.work.exception.WorkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovedWorkCommandService {
    private static final int BREAK_TIME_UNIT = 30;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int ADD_BREAK_TIME_INTERVAL = 4 * MINUTES_IN_HOUR * BREAK_TIME_UNIT;

    private final WorkTypeRepository workTypeRepository;
    private final WorkRepository workRepository;
    private final WorkTimeService workTimeService;
    private final WorkCreateValidator workCreateValidator;

    // ApproveType: WORKCORRECTION, VACATION, BUSINESSTRIP, REMOTEWORK, OVERTIME, CANCEL

    // VacationType: PAID_ETC, UNPAID_ETC, DAYOFF, REFRESH, MILITARY_TRAINING, LIFE_EVENT, (AM_HALF_DAYOFF, PM_HALF_DAYOFF)

    // WorkTypeName: WORK("근무"),
    //    REMOTE_WORK("재택근무"),
    //    VACATION("휴가"),
    //    OVERTIME("연장근무"),
    //    NIGHT("야간근무"),
    //    HOLIDAY("휴일근무"),
    //    BUSINESS_TRIP("출장");

    @Transactional
    public void applyApprovedWork(
            long empId,
            VacationType vacationType,
            ApproveType approveType,
            LocalDate startDate,
            LocalDate endDate,
            LocalDateTime startAt,
            LocalDateTime endAt,
            long workId,
            int breakTime
    ) {
        validateRequestedDates(startDate, endDate);
        switch (approveType) {
            case REMOTEWORK -> applyRemoteWork(empId, startDate, endDate);
            case BUSINESSTRIP -> applyBusinessTrip(empId, startDate, endDate);
            case VACATION -> applyVacation(empId, vacationType, startDate, endDate);
            case WORKCORRECTION -> applyWorkCorrection(empId, startAt, endAt, workId);
            case OVERTIME -> applyOvertime(empId, startAt, endAt, breakTime);
            case RECEIPT, PROPOSAL, CANCEL -> {
            }
        }
    }

    private void applyRemoteWork(long empId, LocalDate startDate, LocalDate endDate) {
        WorkType workType = getWorkType(WorkTypeName.REMOTE_WORK);
        saveMultiDayWork(empId, workType, startDate, endDate);
    }

    private void applyBusinessTrip(long empId, LocalDate startDate, LocalDate endDate) {
        WorkType workType = getWorkType(WorkTypeName.BUSINESS_TRIP);
        saveMultiDayWork(empId, workType, startDate, endDate);
    }

    private void applyVacation(long empId, VacationType vacationType, LocalDate startDate, LocalDate endDate) {
        WorkType workType = getWorkType(WorkTypeName.VACATION);

        // 반일 휴가 처리 시 startDate == endDate 가 보장되어 있다고 가정
        if (vacationType.getVacationType() == VacationTypeEnum.AM_HALF_DAYOFF || vacationType.getVacationType() == VacationTypeEnum.PM_HALF_DAYOFF) {
            LocalDateTime startAt = getStartAt(vacationType, startDate);
            LocalDateTime endAt = getEndAt(vacationType, startDate);
            saveWork(empId, workType, startAt, endAt);
        } else {
            saveMultiDayWork(empId, workType, startDate, endDate);
        }
    }

    private void applyWorkCorrection(long empId, LocalDateTime afterStartAt, LocalDateTime afterEndAt, long workId) {
        Work foundWork = workRepository.findById(workId)
                .orElseThrow(() -> {
                    log.error("Work 조회 실패 - workId={}", workId);
                    return new WorkException(ErrorCode.WORK_NOT_FOUND);
                });

        int breakTime = workTimeService.getBreakTime(afterStartAt, afterEndAt);
        LocalDate correctDate = afterStartAt.toLocalDate();

        foundWork.fromCorrection(afterStartAt, afterEndAt, breakTime);
        final boolean hasAMHalfDayoff = workCreateValidator.hasAMHalfDayOff(empId, correctDate);
        final boolean hasPMHalfDayoff = workCreateValidator.hasPMHalfDayOff(empId, correctDate);

        int requiredWorkMinutes = WorkTimeService.DEFAULT_WORK_HOURS;
        if (hasAMHalfDayoff || hasPMHalfDayoff) {
            requiredWorkMinutes /= 2;
        }

        IsNormalWork isNormalWork = foundWork.isNormalWork(requiredWorkMinutes) ?
                IsNormalWork.Y : IsNormalWork.N;

        foundWork.setIsNormalWork(isNormalWork);
    }

    private void applyOvertime(long empId, LocalDateTime startAt, LocalDateTime endAt, int requestedBreakTime) {
        LocalDateTime minStartAt = startAt.toLocalDate().atTime(workTimeService.getEndTime());
        if (startAt.isBefore(minStartAt)) {
            log.warn("초과근무 시작이 정상근무 종료보다 빠름: startAt={}, minStartAt={}", startAt, minStartAt);
            throw new WorkException(ErrorCode.INVALID_WORK_TIME);
        }

        int minBreakTime = getMinBreakTime(startAt, endAt);
        if (minBreakTime > requestedBreakTime) {
            log.warn("휴게 시간 부족: startAt={}, endAt={}, minBreakTime={}, requestedBreakTime={}", startAt, endAt, minBreakTime, requestedBreakTime);
            throw new WorkException(ErrorCode.NOT_ENOUGH_BREAK_TIME);
        }

        if (Duration.between(startAt, endAt).toMinutes() <= requestedBreakTime) {
            log.warn("휴게 시간 과다: startAt={}, endAt={}, requestedBreakTime={}", startAt, endAt, requestedBreakTime);
            throw new WorkException(ErrorCode.INVALID_WORK_TIME);
        }

        validateRequestedDates(startAt.toLocalDate(), endAt.toLocalDate());
        WorkType workTypeOvertime = getWorkType(WorkTypeName.OVERTIME);
        WorkType workTypeNight = getWorkType(WorkTypeName.NIGHT);
        WorkType workTypeHoliday = getWorkType(WorkTypeName.HOLIDAY);

        LocalDateTime nightStartTime = startAt.toLocalDate().atTime(22, 0);
//        LocalDateTime nightEndTime = startAt.toLocalDate().plusDays(1).atTime(6, 0);

        // 휴일 여부 체크
        boolean isSunday = startAt.toLocalDate().getDayOfWeek() == DayOfWeek.SUNDAY;
        if (isSunday) {
            saveWork(empId, workTypeHoliday, startAt, endAt, requestedBreakTime);
            return;
        }
        boolean isHoliday = workCreateValidator.isHoliday(startAt.toLocalDate());
        if (isHoliday) {
            // 휴일 전체 시간
            saveWork(empId, workTypeHoliday, startAt, endAt, requestedBreakTime);
            return;
        }

        if (!endAt.isAfter(nightStartTime)) {
            // 야간 시간 전까지 끝나면 전부 연장근무
            saveWork(empId, workTypeOvertime, startAt, endAt, requestedBreakTime);
            return;
        }
        if (!startAt.isBefore(nightStartTime)) {
            // 야간 시간부터 시작하면 전부 야간근무
            saveWork(empId, workTypeNight, startAt, endAt, requestedBreakTime);
            return;
        }
        // 연장근무 구간: startAt ~ nightStartTime
        // 야간근무 구간: nightStartTime ~ endAt

        Duration overtimeDuration = Duration.between(startAt, nightStartTime);
        long overtimeMinutes = overtimeDuration.toMinutes();

        // 연장근무에 부여할 최대 휴게시간 = min(요청 휴게시간, 연장근무 시간)
        int breakTimeForOvertime = (int) Math.min(requestedBreakTime, overtimeMinutes);

        // 야간근무에 부여할 나머지 휴게시간
        int breakTimeForNight = requestedBreakTime - breakTimeForOvertime;

        // 연장근무 저장
        saveWork(empId, workTypeOvertime, startAt, nightStartTime, breakTimeForOvertime);

        // 야간근무 저장
        saveWork(empId, workTypeNight, nightStartTime, endAt, breakTimeForNight);

    }

    // 여러 날짜를 포괄하는 재택근무, 출장, 휴가 등 처리
    private void saveMultiDayWork(long empId, WorkType workType, LocalDate startDate, LocalDate endDate) {
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            LocalDateTime startAt = date.atTime(workTimeService.getStartTime());
            LocalDateTime endAt = date.atTime(workTimeService.getEndTime());
            saveWork(empId, workType, startAt, endAt);
            date = date.plusDays(1);
        }
    }

    private void validateRequestedDates(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            log.warn("유효하지 않은 요청 - 시작일이 종료일 이후: startDate={}, endDate={}", startDate, endDate);
            throw new IllegalArgumentException("유효하지 않은 요청");
        }
    }

    private LocalDateTime getStartAt(VacationType vacationType, LocalDate date) {
        if (vacationType.getVacationType() == VacationTypeEnum.PM_HALF_DAYOFF) {
            return date.atTime(workTimeService.getMidTime());
        } else {
            return date.atTime(workTimeService.getStartTime());
        }
    }

    private LocalDateTime getEndAt(VacationType vacationType, LocalDate date) {
        if (vacationType.getVacationType() == VacationTypeEnum.AM_HALF_DAYOFF) {
            return date.atTime(workTimeService.getMidTime());
        } else {
            return date.atTime(workTimeService.getEndTime());
        }
    }

    private int getMinBreakTime(LocalDateTime startAt, LocalDateTime endAt) {
        /* 체류 시간 4시간 이상 -> 30분 부여, 8시간 30분 이상 -> 60분 부여, ... 자동 계산 */
        return (int) (Duration.between(startAt, endAt).toMinutes() + BREAK_TIME_UNIT) / ADD_BREAK_TIME_INTERVAL * BREAK_TIME_UNIT;
    }

    private void saveWork(long empId, WorkType workType, LocalDateTime startAt, LocalDateTime endAt) {
        int breakTime = workTimeService.getBreakTime(startAt, endAt);
        saveWork(empId, workType, startAt, endAt, breakTime);
    }

    // 연장근무, 야간근무는 request로 부터 breakTime 값을 받아와서 별도 처리 필요
    private void saveWork(long empId, WorkType workType, LocalDateTime startAt, LocalDateTime endAt, int breakTime) {
        Work workToInsert = Work.builder()
                .empId(empId)
                .startAt(startAt)
                .endAt(endAt)
                .typeId(workType.getTypeId())
                .breakTime(breakTime)
                .startPushedAt(null)
                .build();

        workRepository.save(workToInsert);
    }


    private WorkType getWorkType(WorkTypeName workTypeName) {
        return workTypeRepository.findByTypeName(workTypeName)
                .orElseThrow(() -> {
                    log.error("WorkType '{}'을(를) 찾을 수 없음", workTypeName);
                    return new WorkException(ErrorCode.WORKTYPE_NOT_FOUND);
                });
    }

}