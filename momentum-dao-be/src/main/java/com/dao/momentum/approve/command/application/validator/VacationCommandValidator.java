package com.dao.momentum.approve.command.application.validator;

import com.dao.momentum.approve.exception.ApproveException;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.dao.momentum.work.command.application.validator.WorkCreateValidator;
import com.dao.momentum.work.command.domain.aggregate.VacationType;
import com.dao.momentum.work.command.domain.aggregate.VacationTypeEnum;
import com.dao.momentum.work.command.domain.repository.VacationTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.stream.Stream;

/* 방학과 관련된 유효성 검사를 위한 클래스 */
@Component
@RequiredArgsConstructor
public class VacationCommandValidator {

    private final WorkCreateValidator workCreateValidator;
    private final VacationTypeRepository vacationTypeRepository;
    private final EmployeeRepository employeeRepository;

    /* 연차, 반차, 리프레시 휴가는 지정된 날만 사용할 수 있게 설정하기 위해 하는 검사*/
    public void validateVacationLimit(
            Long empId, int vacationTypeId, LocalDate startDate, LocalDate endDate
    ) {
        VacationType vacationType =
                vacationTypeRepository.getVacationTypeByVacationTypeId(vacationTypeId);

        VacationTypeEnum vacationTypeEnum = vacationType.getVacationType();

        Employee employee = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

        int dayOff = employee.getRemainingDayoffHours();
        int refresh = employee.getRemainingRefreshDays();

        if (vacationTypeEnum == VacationTypeEnum.PM_HALF_DAYOFF ||
                vacationTypeEnum == VacationTypeEnum.AM_HALF_DAYOFF) {
            if (dayOff < 4) {
                throw new ApproveException(ErrorCode.INSUFFICIENT_HALF_DAY_OFF);
            }
        } else if (vacationTypeEnum == VacationTypeEnum.DAYOFF) {
            int workingDays = calculateWorkingDays(startDate, endDate);
            int requiredHours = workingDays * 8;
            if (dayOff < requiredHours) {
                throw new ApproveException(ErrorCode.INSUFFICIENT_DAY_OFF);
            }
        } else if (vacationTypeEnum == VacationTypeEnum.REFRESH) {
            int workingDays = calculateWorkingDays(startDate, endDate);
            if (refresh < workingDays) {
                throw new ApproveException(ErrorCode.INSUFFICIENT_REFRESH);
            }
        }
    }

    /* 휴가 일수를 계산해주는 메소드 */
    public int calculateWorkingDays(LocalDate start, LocalDate end) {
        return (int) Stream.iterate(start, date -> !date.isAfter(end), date -> date.plusDays(1))
                .filter(date -> !workCreateValidator.isHoliday(date))
                .count();
    }
}
