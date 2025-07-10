package com.dao.momentum.organization.employee.command.application.service;

import com.dao.momentum.organization.employee.command.domain.aggregate.AppointType;
import com.dao.momentum.organization.employee.command.domain.repository.AppointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointRetentionService {
    private static final int SEARCH_YEARS = 2;

    private final AppointRepository appointRepository;

    public int calculateScoreByAppoint(long empId, LocalDate targetDate) {
        LocalDate searchStartDate = targetDate.minusYears(SEARCH_YEARS);
        long count = appointRepository.countAppointsByEmpIdAndRangeOfDateAndType(empId, searchStartDate, targetDate, AppointType.DEPARTMENT_TRANSFER);

        if (count >= 3) {
            return -2;
        }
        if (count >= 2) {
            return -1;
        }
        return 0;
    }

}
