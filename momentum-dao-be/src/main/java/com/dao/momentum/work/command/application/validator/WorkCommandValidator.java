package com.dao.momentum.work.command.application.validator;

import com.dao.momentum.work.command.application.service.WorkTimeService;
import com.dao.momentum.work.command.domain.aggregate.WorkTypeName;
import com.dao.momentum.work.command.domain.repository.WorkRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RequiredArgsConstructor
public abstract class WorkCommandValidator {

    final WorkRepository workRepository;
    final WorkTimeService workTimeService;

    public boolean hasHalfDayOff(long empId, LocalDate date, LocalTime start, LocalTime end) {
        return workRepository.findAllByEmpIdAndDateAndTypeNames(empId, date, List.of(WorkTypeName.VACATION))
                .stream()
                .anyMatch(work -> work.getStartAt().toLocalTime().equals(start) &&
                        work.getEndAt().toLocalTime().equals(end));
    }

    public boolean hasAMHalfDayOff(long empId, LocalDate date) {
        LocalTime startTime = workTimeService.getStartTime();
        LocalTime midTime = workTimeService.getMidTime();
        return hasHalfDayOff(empId, date, startTime, midTime);
    }

    public boolean hasPMHalfDayOff(long empId, LocalDate date) {
        LocalTime midTime = workTimeService.getMidTime();
        LocalTime endTime = workTimeService.getEndTime();
        return hasHalfDayOff(empId, date, midTime, endTime);
    }
}
