package com.dao.momentum.organization.contract.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.contract.command.domain.aggregate.Contract;
import com.dao.momentum.organization.contract.command.domain.aggregate.ContractType;
import com.dao.momentum.organization.contract.command.domain.repository.ContractRepository;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractRetentionService {

    private static final int YEAR_TO_SEARCH = 2;
    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * @param empId      대상 직원 ID
     * @param targetDate 기준 날짜(예: 평가일)
     * @return 0 또는 음수(-13 ~ 0)의 페널티 점수
     */
    @Transactional(readOnly = true)
    public int getScoreBySalaryIncrements(long empId, LocalDate targetDate) {
        Employee employee = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

        if (isUnderThreeYears(employee.getJoinDate(), targetDate)) {
            return 0;
        }

        // 최신 계약 1건 조회
        Contract latest = contractRepository
                .findTop1ByEmpIdAndTypeOrderByCreatedAtDesc(empId, ContractType.SALARY_AGREEMENT)
                .orElse(null);
        if (latest == null) {
            return 0;
        }

        // 2년 전 기준 시점(cutoff) 계산
        LocalDateTime cutoff = targetDate.atStartOfDay().minusYears(YEAR_TO_SEARCH);

        // 2년 이내의 첫 인상 이력 조회
        Optional<Contract> maybePostCutoff = contractRepository
                .findTop1ByEmpIdAndTypeAndCreatedAtAfterOrderByCreatedAtAsc(
                        empId, ContractType.SALARY_AGREEMENT, cutoff);

        // 2년 내 인상이 없으면 동결로 간주
        Contract old = maybePostCutoff.orElse(latest);

        double rate = calculateIncreaseRate(old.getSalary(), latest.getSalary());
        return mapPenalty(rate);
    }


    private boolean isUnderThreeYears(LocalDate joinDate, LocalDate targetDate) {
        return Period.between(joinDate, targetDate).getYears() < 3;
    }

    private double calculateIncreaseRate(BigDecimal oldSalary, BigDecimal newSalary) {
        if (oldSalary.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("Old salary is zero, defaulting increase rate to 0");
            return 0;
        }
        return newSalary.subtract(oldSalary)
                .divide(oldSalary, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .doubleValue();
    }

    private int mapPenalty(double rate) {
        if (rate >= 8) return 0;
        if (rate >= 6) return -4;
        if (rate >= 4) return -7;
        if (rate >= 2) return -10;
        return -13;
    }
}
