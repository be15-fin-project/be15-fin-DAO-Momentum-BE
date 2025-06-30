package com.dao.momentum.approve.command.application.service;

import com.dao.momentum.approve.command.domain.aggregate.Approve;
import com.dao.momentum.approve.command.domain.aggregate.ApproveType;
import com.dao.momentum.approve.exception.ApproveException;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.work.command.domain.aggregate.*;
import com.dao.momentum.work.command.domain.repository.*;
import com.dao.momentum.work.exception.WorkException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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
    private final WorkCorrectionRepository workCorrectionRepository;

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

    private void deleteExistingWork(long empId, LocalDate startDate, LocalDate endDate, WorkTypeName workTypeName) {
        WorkType workType = getWorkType(workTypeName);

        workRepository.deleteByEmployeeIdAndDateRangeAndWorkType(
                empId,
                startDate.atStartOfDay(),
                endDate.plusDays(1).atStartOfDay(),
                workType.getTypeId()
        );
    }

    private WorkType getWorkType(WorkTypeName workTypeName) {
        return workTypeRepository.findByTypeName(workTypeName)
                .orElseThrow(() -> {
                    log.error("WorkType '{}'을(를) 찾을 수 없음", workTypeName);
                    return new WorkException(ErrorCode.WORKTYPE_NOT_FOUND);
                });
    }

}