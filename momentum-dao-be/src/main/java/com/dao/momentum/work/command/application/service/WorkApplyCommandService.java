package com.dao.momentum.work.command.application.service;

import com.dao.momentum.approve.command.domain.aggregate.Approve;
import com.dao.momentum.approve.command.domain.aggregate.ApproveType;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.dao.momentum.work.command.application.validator.WorkCreateValidator;
import com.dao.momentum.work.command.domain.aggregate.*;
import com.dao.momentum.work.command.domain.repository.*;
import com.dao.momentum.work.exception.WorkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.stream.Stream;

import static com.dao.momentum.work.command.application.service.WorkTimeService.DEFAULT_WORK_HOURS;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorkApplyCommandService {
    private static final int BREAK_TIME_UNIT = 30;
    private static final int MINUTES_IN_HOUR = 60;
    private static final int ADD_BREAK_TIME_INTERVAL = 4 * MINUTES_IN_HOUR + BREAK_TIME_UNIT;

    private final WorkTypeRepository workTypeRepository;
    private final WorkRepository workRepository;
    private final WorkTimeService workTimeService;
    private final WorkCreateValidator workCreateValidator;
    private final BusinessTripRepository businessTripRepository;
    private final OvertimeRepository overtimeRepository;
    private final RemoteWorkRepository remoteWorkRepository;
    private final VacationRepository vacationRepository;
    private final VacationTypeRepository vacationTypeRepository;
    private final WorkCorrectionRepository workCorrectionRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public void applyApprovalWork(Approve approve) {
        ApproveType approveType = approve.getApproveType();
        Long empId = approve.getEmpId();
        Long approveId = approve.getApproveId();

        switch (approveType) {
            case OVERTIME -> {
                Overtime overtime = overtimeRepository.findByApproveId(approveId)
                        .orElseThrow(() -> new WorkException(ErrorCode.NOT_EXIST_OVERTIME));

                applyOvertime(empId, overtime.getStartAt(), overtime.getEndAt(), overtime.getBreakTime());
            }

            case VACATION -> {
                Vacation vacation = vacationRepository.findByApproveId(approveId)
                        .orElseThrow(() -> new WorkException(ErrorCode.NOT_EXIST_VACATION));

                VacationType vacationType =
                        vacationTypeRepository.getVacationTypeByVacationTypeId(vacation.getVacationTypeId());

                applyVacation(empId, vacationType, vacation.getStartDate(), vacation.getEndDate());
            }

            case REMOTEWORK -> {
                RemoteWork remote = remoteWorkRepository.findByApproveId(approveId)
                        .orElseThrow(() -> new WorkException(ErrorCode.NOT_EXIST_REMOTE_WORK));

                applyRemoteWork(empId, remote.getStartDate(), remote.getEndDate());
            }

            case BUSINESSTRIP -> {
                BusinessTrip businessTrip = businessTripRepository.findByApproveId(approveId)
                        .orElseThrow(() -> new WorkException(ErrorCode.NOT_EXIST_BUSINESS_TRIP));

                applyBusinessTrip(empId, businessTrip.getStartDate(), businessTrip.getEndDate());
            }

            case WORKCORRECTION -> {
                WorkCorrection correction = workCorrectionRepository.findByApproveId(approveId)
                        .orElseThrow(() -> new WorkException(ErrorCode.NOT_EXIST_WORK_CORRECTION));

                applyWorkCorrection(
                        empId,
                        correction.getAfterStartAt(),
                        correction.getAfterEndAt(),
                        correction.getWorkId()
                );
            }
        }
    }

    // 재택 근무
    private void applyRemoteWork(long empId, LocalDate startDate, LocalDate endDate) {
        WorkType workType = getWorkType(WorkTypeName.REMOTE_WORK);
        if (existsWork(empId, startDate, endDate, WorkTypeName.WORK)) {
            deleteExistingWork(empId, startDate, endDate, WorkTypeName.WORK);
        }
        saveMultiDayWork(empId, workType, startDate, endDate, null);
    }

    // 출장
    private void applyBusinessTrip(long empId, LocalDate startDate, LocalDate endDate) {
        WorkType workType = getWorkType(WorkTypeName.BUSINESS_TRIP);
        if (existsWork(empId, startDate, endDate, WorkTypeName.WORK)) {
            deleteExistingWork(empId, startDate, endDate, WorkTypeName.WORK);
        }
        saveMultiDayWork(empId, workType, startDate, endDate, null);
    }

    // 반차
    private void applyHalfDayoff(long empId, VacationType vacationType, LocalDate startDate) {
        WorkType vacationWorkType = getWorkType(WorkTypeName.VACATION);

        boolean isAMHalfDayoff = vacationType.getVacationType() == VacationTypeEnum.AM_HALF_DAYOFF;
        boolean isPMHalfDayoff = vacationType.getVacationType() == VacationTypeEnum.PM_HALF_DAYOFF;

        LocalTime startTime = workTimeService.getStartTime();
        LocalTime midTime = workTimeService.getMidTime();
        LocalTime endTime = workTimeService.getEndTime();

        LocalDateTime halfDayStart = isAMHalfDayoff ?
                startDate.atTime(midTime) : startDate.atTime(startTime);
        LocalDateTime halfDayEnd = isPMHalfDayoff ?
                startDate.atTime(midTime) : startDate.atTime(endTime);

        final int requiredWorkMinutes = DEFAULT_WORK_HOURS * 60 / 2;

        final Integer vacationTypeId = vacationType.getVacationTypeId();

        // 1. 기존 출근 기록 조회 후 있으면 수정
        workRepository.findByEmpIdAndDateAndTypeName(empId, startDate, WorkTypeName.WORK)
                .ifPresent(work -> {
                    if (isAMHalfDayoff) {
                        LocalDateTime adjustedStart = work.getStartAt().isBefore(halfDayStart) ?
                                halfDayStart : work.getStartAt();
                        LocalDateTime endAt = work.getEndAt();
                        int breakTime = workTimeService.getBreakTime(adjustedStart, endAt);
                        work.fromCorrection(adjustedStart, endAt, breakTime, vacationTypeId);
                        IsNormalWork isNormalWork = work.isNormalWork(requiredWorkMinutes) ?
                                IsNormalWork.Y : IsNormalWork.N;
                        work.setIsNormalWork(isNormalWork);
                    } else if (isPMHalfDayoff) {
                        LocalDateTime adjustedEnd = work.getEndAt().isAfter(halfDayEnd) ?
                                halfDayEnd : work.getEndAt();
                        LocalDateTime startAt = work.getStartAt();
                        int breakTime = workTimeService.getBreakTime(startAt, adjustedEnd);
                        work.fromCorrection(startAt, adjustedEnd, breakTime, vacationTypeId);
                        IsNormalWork isNormalWork = work.isNormalWork(requiredWorkMinutes) ?
                                IsNormalWork.Y : IsNormalWork.N;
                        work.setIsNormalWork(isNormalWork);
                    }
                    workRepository.save(work);
                });

        // 2. 휴가 기록 새로 삽입
        LocalDateTime vacationStartAt = getStartAt(vacationType, startDate);
        LocalDateTime vacationEndAt = getEndAt(vacationType, startDate);

        Work newVacationWork = Work.builder()
                .empId(empId)
                .typeId(vacationWorkType.getTypeId())
                .startAt(vacationStartAt)
                .endAt(vacationEndAt)
                .breakTime(workTimeService.getBreakTime(vacationStartAt, vacationEndAt))
                .build();

        IsNormalWork isNormalWork = newVacationWork.isNormalWork(requiredWorkMinutes) ?
                IsNormalWork.Y : IsNormalWork.N;

        newVacationWork.setIsNormalWork(isNormalWork);

        workRepository.save(newVacationWork);

        // 3. 회원 테이블에 반차 시간 만큼 차감
        Employee employee =  employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

        employee.updateRemainingDayOff(employee.getRemainingDayoffHours() - 4);

    }

    // 휴가
    private void applyVacation(long empId, VacationType vacationType, LocalDate startDate, LocalDate endDate) {
        WorkType workType = getWorkType(WorkTypeName.VACATION);
        Integer vacationTypeId = vacationType.getVacationTypeId();
        VacationTypeEnum vacationTypeEnum = vacationType.getVacationType();

        boolean isAMHalfDayoff = vacationTypeEnum == VacationTypeEnum.AM_HALF_DAYOFF;
        boolean isPMHalfDayoff = vacationTypeEnum == VacationTypeEnum.PM_HALF_DAYOFF;

        /* 1. 반차인 경우 따로 적용하기 */
        if (isAMHalfDayoff || isPMHalfDayoff) {
            applyHalfDayoff(empId, vacationType, startDate);
            return;
        }

        // 2. 나머지 휴가인 경우
        // 기존 근무 데이터 삭제
        if (existsWork(empId, startDate, endDate, WorkTypeName.WORK)) {
            deleteExistingWork(empId, startDate, endDate, WorkTypeName.WORK);
        }
        // 휴가 기록 새로 저장
        saveMultiDayWork(empId, workType, startDate, endDate, vacationTypeId);

        // 회원 테이블에 연차 혹은 리프레시 휴가 차감
        Employee employee =  employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

        if(vacationTypeEnum == VacationTypeEnum.DAYOFF) {
            int dayOff = (int) Stream.iterate(startDate, date -> !date.isAfter(endDate), date -> date.plusDays(1))
                    .filter(date -> !workCreateValidator.isHoliday(date))  // 휴일이 아닌 날만 필터링
                    .count();

            employee.updateRemainingDayOff(employee.getRemainingDayoffHours() - dayOff * 8); // 연차인 경우 휴가 날짜 만큼 차감
        } else if(vacationTypeEnum == VacationTypeEnum.REFRESH) {
            int vacationDays = (int) Stream.iterate(startDate, date -> !date.isAfter(endDate), date -> date.plusDays(1))
                    .filter(date -> !workCreateValidator.isHoliday(date))  // 휴일이 아닌 날만 필터링
                    .count();

            employee.updateRemainingRefreshDay(employee.getRemainingRefreshDays() - vacationDays); // 다른 경우엔 리프레시 휴가 차감
        }
    }

    // 출퇴근 정정
    private void applyWorkCorrection(long empId, LocalDateTime afterStartAt, LocalDateTime afterEndAt, long workId) {
        Work foundWork = workRepository.findById(workId)
                .orElseThrow(() -> {
                    log.error("Work 조회 실패 - workId={}", workId);
                    return new WorkException(ErrorCode.WORK_NOT_FOUND);
                });

        int breakTime = workTimeService.getBreakTime(afterStartAt, afterEndAt);
        LocalDate correctDate = afterStartAt.toLocalDate();

        Integer vacationTypeId = null;

        foundWork.fromCorrection(afterStartAt, afterEndAt, breakTime, vacationTypeId);
        final boolean hasAMHalfDayoff = workCreateValidator.hasAMHalfDayOff(empId, correctDate);
        final boolean hasPMHalfDayoff = workCreateValidator.hasPMHalfDayOff(empId, correctDate);

        int requiredWorkMinutes = DEFAULT_WORK_HOURS;
        if (hasAMHalfDayoff || hasPMHalfDayoff) {
            requiredWorkMinutes /= 2;
        }

        IsNormalWork isNormalWork = foundWork.isNormalWork(requiredWorkMinutes) ?
                IsNormalWork.Y : IsNormalWork.N;

        foundWork.setIsNormalWork(isNormalWork);
        workRepository.save(foundWork);
    }

    // 초과 근무
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
    private void saveMultiDayWork(long empId, WorkType workType, LocalDate startDate, LocalDate endDate, Integer vacationTypeId) {
        LocalDate date = startDate;

        while (!date.isAfter(endDate)) {
            LocalDateTime startAt = date.atTime(workTimeService.getStartTime());
            LocalDateTime endAt = date.atTime(workTimeService.getEndTime());
            int breakTime = workTimeService.getBreakTime(startAt, endAt);
            saveWork(empId, workType, startAt, endAt, breakTime, vacationTypeId);
            date = date.plusDays(1);
        }
    }

    private void deleteExistingWork(long empId, LocalDate startDate, LocalDate endDate, WorkTypeName workTypeName) {
        WorkType workType = getWorkType(workTypeName);

        workRepository.deleteByEmployeeIdAndDateRangeAndWorkType(
                empId,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay(),
                workType.getTypeId()
        );
    }

    private boolean existsWork(long empId, LocalDate startDate, LocalDate endDate, WorkTypeName workTypeName) {
        WorkType workType = getWorkType(workTypeName);

        return workRepository.existsByEmpIdAndDateRangeAndWorkType(
                empId,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay(),
                workType.getTypeId()
        );
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
        saveWork(empId, workType, startAt, endAt, breakTime, null);
    }

    // 휴가일 경우 vacationTypeId insert 필요
    private void saveWork(long empId, WorkType workType, LocalDateTime startAt, LocalDateTime endAt, int breakTime, Integer vacationTypeId) {
        Work workToInsert = Work.builder()
                .empId(empId)
                .startAt(startAt)
                .endAt(endAt)
                .typeId(workType.getTypeId())
                .breakTime(breakTime)
                .vacationTypeId(vacationTypeId)
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