package com.dao.momentum.organization.employee.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.employee.command.domain.aggregate.Appoint;
import com.dao.momentum.organization.employee.command.domain.aggregate.AppointType;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.AppointRepository;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointRetentionService {
    private static final int SEARCH_YEARS_FOR_DEPT_TRANSTER = 2;
    private static final int SEARCH_YEARS_FOR_PROMOTION = 6;
    private static final LocalDate SERVICE_START_DATE = LocalDate.of(2023, 1, 21);


    private final AppointRepository appointRepository;
    private final EmployeeRepository employeeRepository;

    public int calculateScoreByDeptTransfer(long empId, LocalDate targetDate) {
        LocalDate searchStartDate = targetDate.minusYears(SEARCH_YEARS_FOR_DEPT_TRANSTER);
        long count = appointRepository.countAppointsByEmpIdAndRangeOfDateAndType(empId, searchStartDate, targetDate, AppointType.DEPARTMENT_TRANSFER);

        if (count >= 3) {
            return -2;
        }
        if (count >= 2) {
            return -1;
        }
        return 0;
    }

    public int calculateScoreByPromotion(long empId, LocalDate targetDate) {
        Employee employee = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

        LocalDate searchStartDate = targetDate.minusYears(SEARCH_YEARS_FOR_PROMOTION);

        // 지정된 기간 동안 모든 승진 이력 조회
        List<Appoint> promotions = appointRepository.findAllAppointsByEmpIdAndRangeOfDateAndType(
                empId, searchStartDate, targetDate, AppointType.PROMOTION);

        // 가장 마지막 승진 날짜 찾기
        LocalDate lastPromotionDate = promotions.stream()
                .map(Appoint::getAppointDate)
                .max(LocalDate::compareTo)
                .orElse(null);

        int years;

        // 마지막 승진이 없으면 근속 연수 계산
        if (lastPromotionDate == null) {
            LocalDate joinDate = employee.getJoinDate();

            // 기준 시작일은 서비스 시작일 이후만 유효 (기존 발령 내역은 서비스에 등록 안됨)
            LocalDate from = joinDate.isBefore(SERVICE_START_DATE) ? SERVICE_START_DATE : joinDate;
            years = calculateYearsBetween(from, targetDate);
        } else {
            years = calculateYearsBetween(lastPromotionDate, targetDate);
        }

        return mapPromotionPenalty(years);
    }

    private int calculateYearsBetween(LocalDate from, LocalDate to) {
        return Period.between(from, to).getYears();
    }

    private int mapPromotionPenalty(int years) {
        if (years >= 6) return -7;
        if (years >= 5) return -5;
        if (years >= 4) return -3;
        return 0;
    }

}
