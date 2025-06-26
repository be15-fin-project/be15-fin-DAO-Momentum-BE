package com.dao.momentum.organization.employee.command.application.service;

import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VacationTimeCommandService {
    private static final int WORKING_HOURS_IN_DAY = 8;
    private static final int MONTHS_IN_YEAR = 12;
    private static final int STANDARD_DAYOFF = 15;
    private static final int MAXIMUM_DAYOFF = 25;

    private final EmployeeRepository employeeRepository;

    /* 매년 1월 1일 자정에 배치 작업 실행 */
    @Scheduled(cron = "0 0 0 1 1 *")
    @Transactional
    public void updateVacationTimes() {
        LocalDateTime start = LocalDateTime.now();
        log.info("[휴가 일수 Batch System] 휴가 일수 갱신 시작");
        int targetYear = LocalDate.now().getYear();

        List<Employee> employees = employeeRepository.findAllByStatus(Status.EMPLOYED); // 모든 직원

        employees.forEach(
          emp -> {
              LocalDate joinDate = emp.getJoinDate();
              int joinYear = joinDate.getYear();
              int joinMonth = joinDate.getMonthValue();

              int dayoffHours = computeDayoffHours(joinYear, joinMonth, targetYear);
              int refreshDays = computeRefreshDays(joinYear, targetYear);

              emp.updateVacations(dayoffHours, refreshDays);
          }
        );

        LocalDateTime end = LocalDateTime.now();
        long duration = Duration.between(start, end).toSeconds();
        log.info("[휴가 일수 Batch System] {}년 연차 갱신 완료 - 총 {}명, 소요 시간 - {}초", targetYear, employees.size(), duration);
        // 실패 시 로그는 spring이 남겨줌
    }

    int computeDayoffDays(int joinYear, int joinMonth, int targetYear) {
        int yearsWorked = targetYear - joinYear;

        if (yearsWorked < 0) {
            return 0;
        }

        if (yearsWorked == 0) { // 입사한 해
            return Math.max(0, MONTHS_IN_YEAR - joinMonth);
        }

        if (yearsWorked == 1) {
            int monthsNotWorked = joinMonth - 1;
            double weight = (double) monthsNotWorked / MONTHS_IN_YEAR;

            return (int) Math.ceil(
                    STANDARD_DAYOFF - (STANDARD_DAYOFF - 12) * weight
            );
        }

        // 2년차 이상 -> 3년차부터, 2년마다 1일 추가 지급
        int dayoffDays = STANDARD_DAYOFF + (yearsWorked - 1) / 2;
        return Math.min(MAXIMUM_DAYOFF, dayoffDays);
    }

    int computeDayoffHours(int joinYear, int joinMonth, int targetYear) {
        return computeDayoffDays(joinYear, joinMonth, targetYear) * WORKING_HOURS_IN_DAY;
    }

    int computeRefreshDays(int joinYear, int targetYear) {
        int yearsWorked = targetYear - joinYear;

        return switch (yearsWorked) {
            case 3 -> 3;
            case 5 -> 5;
            case 10 -> 10;
            default -> 0;
        };
    }


}
