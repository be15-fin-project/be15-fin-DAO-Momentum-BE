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
    public int calculateScoreBySalaryIncrements(long empId, LocalDate targetDate) {
        log.info("[연봉 인상률 감점 계산 시작] empId={}, 기준일={}", empId, targetDate);

        Employee employee = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

        if (isUnderThreeYears(employee.getJoinDate(), targetDate)) {
            log.info("- 입사 3년 미만으로 감점 제외 - empId={}, joinDate={}", empId, employee.getJoinDate());
            return 0;
        }

        // 최신 계약 1건 조회
        Contract latest = contractRepository
                .findTop1ByEmpIdAndTypeOrderByCreatedAtDesc(empId, ContractType.SALARY_AGREEMENT)
                .orElse(null);
        if (latest == null) {
            log.info("- 연봉 계약 없음 - empId={}", empId);
            return 0;
        }

        // 2년 전 기준 시점(cutoff) 계산
        LocalDateTime cutoff = targetDate.atStartOfDay().minusYears(YEAR_TO_SEARCH);
        Contract old = findOldContract(empId, cutoff, latest);

        log.info("- 최신 계약: {}, 과거 계약: {}", latest.getSalary(), old.getSalary());

        double rate = calculateIncreaseRate(old.getSalary(), latest.getSalary());
        int penalty = mapPenalty(rate);

        log.info("- 연봉 인상률 계산 - oldSalary={}, newSalary={}, rate={}%, penalty={}",
                old.getSalary(), latest.getSalary(), String.format("%.2f", rate), penalty);

        return mapPenalty(rate);
    }

    private boolean isUnderThreeYears(LocalDate joinDate, LocalDate targetDate) {
        return Period.between(joinDate, targetDate).getYears() < 3;
    }

    /**
     * 2년 전 시점까지 과거 계약 중 최신, 없으면 2년 내 첫 계약,
     * 둘 다 없으면 최신(latest)으로 동결
     */
    private Contract findOldContract(long empId, LocalDateTime cutoff, Contract latest) {
        Optional<Contract> beforeCutoff = contractRepository
                .findTop1ByEmpIdAndTypeAndCreatedAtBeforeOrderByCreatedAtDesc(
                        empId, ContractType.SALARY_AGREEMENT, cutoff);

        Optional<Contract> afterCutoff = contractRepository
                .findTop1ByEmpIdAndTypeAndCreatedAtAfterOrderByCreatedAtAsc(
                        empId, ContractType.SALARY_AGREEMENT, cutoff);

        Contract result = beforeCutoff.orElse(afterCutoff.orElse(latest));

        log.info("- 감점 비교용 계약 결정 - cutoff={}, 선택된 계약 생성일={}", cutoff.toLocalDate(), result.getCreatedAt().toLocalDate());

        return beforeCutoff.orElse(
                afterCutoff.orElse(latest)
        );
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
