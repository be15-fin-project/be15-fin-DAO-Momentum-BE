package com.dao.momentum.approve.command.application.service;

import com.dao.momentum.approve.command.domain.aggregate.Approve;
import com.dao.momentum.approve.command.domain.aggregate.ApproveType;
import com.dao.momentum.approve.exception.ApproveException;
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

import java.time.LocalDate;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApprovalCancelCommandService {

    private final WorkRepository workRepository;
    private final WorkTypeRepository workTypeRepository;
    private final BusinessTripRepository businessTripRepository;
    private final OvertimeRepository overtimeRepository;
    private final RemoteWorkRepository remoteWorkRepository;
    private final VacationRepository vacationRepository;
    private final VacationTypeRepository vacationTypeRepository;
    private final WorkCorrectionRepository workCorrectionRepository;
    private final EmployeeRepository employeeRepository;
    private final WorkCreateValidator workCreateValidator;

    /*
    * 근태와 관련된 결재 내역을 취소하는 로직
    * (1) 초과 근무, 휴가, 재택 근무, 출장 내역은 work 테이블에서 데이터 삭제
    * (2) 출퇴근 내역 정정은 원래 시간으로 정정
    * */
    @Transactional
    public void cancelApprovalWork(Approve approve) {
        ApproveType approveType = approve.getApproveType();
        Long empId = approve.getEmpId();
        Long approveId = approve.getApproveId();

        switch (approveType) {
            case OVERTIME -> {
                Overtime overtime = overtimeRepository.findByApproveId(approveId)
                        .orElseThrow(() -> new WorkException(ErrorCode.NOT_EXIST_OVERTIME));

                LocalDate startDate = overtime.getStartAt().toLocalDate();
                LocalDate endDate = overtime.getEndAt().toLocalDate();

                deleteExistingWork(empId, startDate, endDate, WorkTypeName.OVERTIME);
            }

            case VACATION -> {
                Vacation vacation = vacationRepository.findByApproveId(approveId)
                        .orElseThrow(() -> new WorkException(ErrorCode.NOT_EXIST_VACATION));

                LocalDate startDate = vacation.getStartDate();
                LocalDate endDate = vacation.getEndDate();

                deleteExistingWork(empId, startDate, endDate, WorkTypeName.VACATION);

                // 리프레시 휴가 수 복구하기
                VacationType vacationType =
                        vacationTypeRepository.getVacationTypeByVacationTypeId(vacation.getVacationTypeId());

                // 휴가 enum 값
                VacationTypeEnum vacationTypeEnum =
                        vacationType.getVacationType();

                Employee employee =  employeeRepository.findByEmpId(empId)
                        .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

                // 연차, 반차, 그 외에 휴가를 구분
                if(vacationTypeEnum == VacationTypeEnum.PM_HALF_DAYOFF ||
                        vacationTypeEnum == VacationTypeEnum.AM_HALF_DAYOFF) {
                    // 1. 반차인 경우엔 4시간 추가
                    employee.updateRemainingDayOff(employee.getRemainingDayoffHours() + 4);
                } else if (vacationTypeEnum == VacationTypeEnum.DAYOFF){
                    // 2. 연차인 경우엔 8시간 추가
                    employee.updateRemainingDayOff(employee.getRemainingDayoffHours() + 8);

                } else{
                    // 3. 리프레시 휴가인 경우
                    int vacationDays = (int) Stream.iterate(startDate, date -> !date.isAfter(endDate), date -> date.plusDays(1))
                            .filter(date -> !workCreateValidator.isHoliday(date))  // 휴일이 아닌 날만 필터링
                            .count();

                    employee.updateRemainingRefreshDay(employee.getRemainingRefreshDays() + vacationDays);
                }

            }

            case REMOTEWORK -> {
                RemoteWork remote = remoteWorkRepository.findByApproveId(approveId)
                        .orElseThrow(() -> new WorkException(ErrorCode.NOT_EXIST_REMOTE_WORK));

                LocalDate startDate = remote.getStartDate();
                LocalDate endDate = remote.getEndDate();

                deleteExistingWork(empId, startDate, endDate, WorkTypeName.REMOTE_WORK);
            }

            case BUSINESSTRIP -> {
                BusinessTrip businessTrip = businessTripRepository.findByApproveId(approveId)
                        .orElseThrow(() -> new WorkException(ErrorCode.NOT_EXIST_BUSINESS_TRIP));

                LocalDate startDate = businessTrip.getStartDate();
                LocalDate endDate = businessTrip.getEndDate();

                deleteExistingWork(empId, startDate, endDate, WorkTypeName.BUSINESS_TRIP);
            }

            case WORKCORRECTION -> {
                WorkCorrection correction = workCorrectionRepository.findByApproveId(approveId)
                        .orElseThrow(() -> new WorkException(ErrorCode.NOT_EXIST_WORK_CORRECTION));

                long workId = correction.getWorkId();

                Work work = workRepository.findById(workId)
                        .orElseThrow(() -> new WorkException(ErrorCode.WORK_NOT_FOUND));

                // 기존 출퇴근 시간으로 변경하기
                work.changeBeforeWorkTime(correction.getBeforeStartAt(), correction.getBeforeEndAt());
            }

            case CANCEL -> {
                // 이미 취소된 결재는 다시 취소할 수 없음
                throw new ApproveException(ErrorCode.APPROVAL_ALREADY_CANCELED);
            }
        }
    }

    /* 근무 내역을 삭제하는 메소드 */
    private void deleteExistingWork(long empId, LocalDate startDate, LocalDate endDate, WorkTypeName workTypeName) {
        WorkType workType = getWorkType(workTypeName);

        workRepository.deleteByEmployeeIdAndDateRangeAndWorkType(
                empId,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay(),
                workType.getTypeId()
        );
    }

    /* 근무 종류를 찾는 메소드 */
    private WorkType getWorkType(WorkTypeName workTypeName) {
        return workTypeRepository.findByTypeName(workTypeName)
                .orElseThrow(() -> {
                    log.error("WorkType '{}'을(를) 찾을 수 없음", workTypeName);
                    return new WorkException(ErrorCode.WORKTYPE_NOT_FOUND);
                });
    }

}