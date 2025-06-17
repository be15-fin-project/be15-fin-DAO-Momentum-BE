package com.dao.momentum.work.command.application.validator;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.company.command.domain.repository.HolidayRepository;
import com.dao.momentum.work.command.application.service.WorkTimeService;
import com.dao.momentum.work.command.domain.aggregate.Work;
import com.dao.momentum.work.command.domain.aggregate.WorkType;
import com.dao.momentum.work.command.domain.aggregate.WorkTypeName;
import com.dao.momentum.work.command.domain.repository.WorkRepository;
import com.dao.momentum.work.command.domain.repository.WorkTypeRepository;
import com.dao.momentum.work.exception.WorkException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Component
public class WorkCreateValidator extends WorkCommandValidator {

    private final WorkTypeRepository workTypeRepository;
    private final HolidayRepository holidayRepository;

    public WorkCreateValidator(
            WorkRepository workRepository,
            WorkTypeRepository workTypeRepository,
            WorkTimeService workTimeService,
            HolidayRepository holidayRepository
    ) {
        super(workRepository, workTimeService); // 부모 클래스의 생성자 호출
        this.workTypeRepository = workTypeRepository;
        this.holidayRepository = holidayRepository;
    }

    public void validateWorkCreation(long empId, LocalDate today, LocalDateTime startPushedAt, LocalDateTime startAt, LocalDateTime endAt) {
        if (workAlreadyRecorded(empId, today)) {
            throw new WorkException(ErrorCode.WORK_ALREADY_RECORDED);
        }

        if (hasApprovedWork(empId, today)) {
            throw new WorkException(ErrorCode.ACCEPTED_WORK_ALREADY_RECORDED);
        }

        if (isHoliday(today)) {
            throw new WorkException(ErrorCode.WORK_REQUESTED_ON_HOLIDAY);
        }

        validateStartAt(startAt, endAt, startPushedAt, today);
    }

    // 이미 등록된 출근 기록이 있는 지 체크
    public boolean workAlreadyRecorded(long empId, LocalDate date) {
        return workRepository.existsByEmpIdAndStartAtDateAndTypeName(empId, date, WorkTypeName.WORK);
    }

    // 승인된 휴가/출장/재택 근무가 있는 지 체크
    public boolean hasApprovedWork(long empId, LocalDate date) {
        List<WorkTypeName> typeNames = List.of(
                WorkTypeName.REMOTE_WORK,
                WorkTypeName.VACATION,
                WorkTypeName.BUSINESS_TRIP
        );

        List<Work> works = workRepository.findAllByEmpIdAndDateAndTypeNames(empId, date, typeNames);

        LocalTime startTime = workTimeService.getStartTime();
        LocalTime endTime = workTimeService.getEndTime();

        LocalDateTime startAt = LocalDateTime.of(date, startTime);
        LocalDateTime endAt = LocalDateTime.of(date, endTime);

        for (Work foundWork : works) {
            int typeId = foundWork.getTypeId();
            WorkType type = workTypeRepository.findById(typeId)
                    .orElseThrow(() -> new WorkException(ErrorCode.WORKTYPE_NOT_FOUND));

            WorkTypeName typeName = type.getTypeName();

            if (typeName == WorkTypeName.REMOTE_WORK || typeName == WorkTypeName.BUSINESS_TRIP) {
                return true;
            }

            if (typeName == WorkTypeName.VACATION) {

                // 반차가 아닌 일반 휴가 중 겹치는 일정이 있는지 체크
                LocalDateTime vacationStart = foundWork.getStartAt();
                LocalDateTime vacationEnd = foundWork.getEndAt();

                boolean overlaps = vacationStart.equals(startAt) && vacationEnd.equals(endAt);

                if (overlaps) return true;

            }
        }

        return false;
    }

    // 휴일인지 체크
    public boolean isHoliday(LocalDate date) {
        if (date.getDayOfWeek() == DayOfWeek.SUNDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY) {
            return true;
        }

        return holidayRepository.existsByDate(date);
    }

    public void validateStartAt(LocalDateTime startAt, LocalDateTime endAt, LocalDateTime startPushedAt, LocalDate today) {
        LocalDate pushedDate = startPushedAt.toLocalDate();

        if (!pushedDate.isEqual(today)) {
            log.warn("잘못된 출근 요청 - 출근 시각이 오늘 날짜가 아님: startDate={}, today={}", startAt.toLocalDate(), today);
            throw new WorkException(ErrorCode.INVALID_WORK_TIME);
        }

        if (!startAt.isBefore(endAt)) {
            log.warn("잘못된 출근 요청 - 출근 시각이 종료 시각보다 늦거나 같음: startAt={}, endAt={}", startAt, endAt);
            throw new WorkException(ErrorCode.INVALID_WORK_TIME);
        }

    }


}
