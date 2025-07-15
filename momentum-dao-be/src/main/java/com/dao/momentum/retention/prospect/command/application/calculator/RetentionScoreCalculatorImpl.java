package com.dao.momentum.retention.prospect.command.application.calculator;

import com.dao.momentum.evaluation.kpi.query.service.KpiRetentionService;
import com.dao.momentum.organization.contract.command.application.service.ContractRetentionService;
import com.dao.momentum.organization.employee.command.application.service.AppointRetentionService;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.retention.prospect.command.application.dto.request.RetentionSupportDto;
import com.dao.momentum.evaluation.eval.query.service.EvaluationScoreService;
import com.dao.momentum.work.command.application.service.WlbRetentionService;
import com.dao.momentum.work.command.application.service.WorkRetentionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetentionScoreCalculatorImpl implements RetentionScoreCalculator {

    private final EvaluationScoreService evaluationScoreService;
    private final ContractRetentionService contractRetentionService;
    private final AppointRetentionService appointRetentionService;
    private final WorkRetentionService workRetentionService;
    private final KpiRetentionService kpiRetentionService;
    private final WlbRetentionService wlbRetentionService;

    /**
     * 각 항목별 점수와 전체 점수를 계산하는 로직
     *  */
    @Override
    public RetentionSupportDto calculate(Integer year, Integer month, Employee employee) {
        log.info("[근속 점수 계산 시작] empNo={}, year={}, month={}", employee.getEmpNo(), year, month);

        int jobLevel = calculateJobLevel(year, month, employee);           // 직무 만족도 : 최대 20점
        int compLevel = calculateCompLevel(year, month, employee);         // 임금 및 복지 만족도 : 최대 20점
        int relationLevel = calculateRelationLevel(year, month, employee); // 상사, 동료 관계 : 최대 15점
        int growthLevel = calculateGrowthLevel(year, month, employee);     // 경력 개발 기회 : 최대 15점
        int tenureLevel = calculateTenureLevel(year, month, employee);     // 근속 연수, 근태 : 최대 15점
        int wlbLevel = calculateWlbLevel(year, month, employee);           // 워라밸, 초과 근무 : 최대 15점

        BigDecimal ageCoefficient = getAgeCoefficient(year, employee); // 보정 계수

        // 총점 구하기
        BigDecimal retentionScore = weightedScore(
                jobLevel, compLevel, relationLevel, growthLevel, tenureLevel, wlbLevel, ageCoefficient
        );

        log.info("[근속 점수 계산 완료] empNo={}, 점수(직무={}, 보상={}, 관계={}, 성장={}, 근속={}, 워라밸={}), 보정계수={}, 총점={}",
                employee.getEmpNo(), jobLevel, compLevel, relationLevel, growthLevel, tenureLevel, wlbLevel, ageCoefficient, retentionScore);

        // 각각의 총점을 100점으로 환산
        // 총점이 20점 -> 최종 점수 * 5
        // 총점이 15점 -> 최종 점수 * 20 / 3
        return new RetentionSupportDto(
                jobLevel * 5,
                compLevel * 5,
                (int) Math.round(relationLevel * 20.0 / 3),
                (int) Math.round(growthLevel * 20.0 / 3),
                (int) Math.round((tenureLevel + 2) * 20.0 / 3),
                (int) Math.round(wlbLevel * 20.0 / 3),
                retentionScore
        );
    }

    /**
     * 각각의 점수를 계산하는 로직
     * */

    /* 직무 만족도 계산 메소드 */
    private int calculateJobLevel(Integer year, Integer month, Employee emp) {
        log.info("- 직무 만족도 계산 시작 - empNo={}", emp.getEmpNo());

        int jobScore = 20;

        // 1. 인사 평가
        jobScore += evaluationScoreService.getAdjustedScoreForForm(4, emp.getEmpId(), 10.0, year, month);

        // 2. 평가 변화
        jobScore += evaluationScoreService.getHrGradeDropPenalty(emp.getEmpId(), year, month);

        // 3. 평가
        jobScore += evaluationScoreService.getAdjustedScoreForForm(5, emp.getEmpId(), 4.0, year, month);
        jobScore += evaluationScoreService.getAdjustedScoreForForm(11, emp.getEmpId(), 4.0, year, month);

        int finalScore = clampScore(jobScore, 20);
        log.info("- 직무 만족도 계산 완료 - 결과={}", finalScore);
        return clampScore(jobScore, 20);
    }

    /* 경력 개발 기회 계산 메소드 */
    private int calculateCompLevel(Integer year, Integer month, Employee emp) {
        log.info("- 보상 만족도 계산 시작 - empNo={}", emp.getEmpNo());

        int compScore = 20;
        long empId = emp.getEmpId();
        LocalDate targetDate = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1); // 대상 달(과거 날짜)의 마지막 날

        // 1. 연봉 (최대 -13점)
        // 0 또는 감점을 적용한 음수이므로 더해준다. (예시: 20 + (-5) = 15)
        compScore += contractRetentionService.calculateScoreBySalaryIncrements(empId, targetDate);

        // 2. 복리후생 (최대 -7점)
        compScore += evaluationScoreService.getAdjustedScoreForForm(8, emp.getEmpId(), 7.0, year, month);

        int finalScore = clampScore(compScore, 20);
        log.info("- 보상 만족도 계산 완료 - 결과={}", finalScore);

        return clampScore(compScore, 20);
    }

    /* 상사, 동료 관계 계산 메소드 */
    private int calculateRelationLevel(Integer year, Integer month, Employee emp) {
        log.info("- 관계 만족도 계산 시작 - empNo={}", emp.getEmpNo());

        int relationScore = 15;
        long empId = emp.getEmpId();
        LocalDate targetDate = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);

        // 1. 다면 평가
        relationScore += evaluationScoreService.getAdjustedScoreForForms(List.of(1, 2, 3), emp.getEmpId(), 8.0, year, month);

        // 2. 조직 문화 평가
        relationScore += evaluationScoreService.getAdjustedScoreForForm(6, emp.getEmpId(), 5.0, year, month);

        // 3. 발령 이력 (최대 -2점)
        relationScore += appointRetentionService.calculateScoreByDeptTransfer(empId, targetDate);

        int finalScore = clampScore(relationScore, 15);
        log.info("- 관계 만족도 계산 완료 - 결과={}", finalScore);

        return clampScore(relationScore, 15);
    }

    /* 경력 개발 기회 계산 메소드 */
    private int calculateGrowthLevel(Integer year, Integer month, Employee emp) {
        log.info("- 성장 만족도 계산 시작 - empNo={}", emp.getEmpNo());

        int growthScore = 15;
        LocalDate targetDate = LocalDate.of(year, month, 1).plusMonths(1).minusDays(1);

        // 1. 승진 정체 (최대 -7점)
        growthScore += appointRetentionService.calculateScoreByPromotion(emp.getEmpId(), targetDate);

        // 2. KPI 감점
        growthScore += kpiRetentionService.calculateTotalKpiPenalty(emp.getEmpId(), year, month);

        // 3. 조직 공정성 평가
        growthScore += evaluationScoreService.getAdjustedScoreForForm(7, emp.getEmpId(), 4.0, year, month);

        int finalScore = clampScore(growthScore, 15);
        log.info("- 성장 만족도 계산 완료 - 결과={}", finalScore);

        return clampScore(growthScore, 15);
    }

    /* 근속 연수, 근태 계산 메소드 */
    private int calculateTenureLevel(Integer year, Integer month, Employee emp) {
        log.info("- 근속 만족도 계산 시작 - empNo={}", emp.getEmpNo());

        double tenureScore = 15;
        LocalDate targetDate = LocalDate.of(year, month, 1).plusMonths(1);
        long empId = emp.getEmpId();

        // 1. 근속 연수 (최대 -9점)
        tenureScore += workRetentionService.calculateScoreByWorkedMonths(empId, targetDate.minusDays(1));

        // 2. 근태 이력 (최대 -6점)
        // 메서드 로직 상 대상 달 다음달의 1일이 필요 (이 값을 변수 targetDate로 정의)
        tenureScore += workRetentionService.calculateScoreByAbsenceAndLate(empId, targetDate);

        int finalScore = clampScore((int) tenureScore, 15);
        log.info("- 근속 만족도 계산 완료 - 결과={}", finalScore);

        return clampScore((int) tenureScore, 15);
    }

    /* 워라밸, 초과근무 계산 메소드 */
    private int calculateWlbLevel(Integer year, Integer month, Employee emp) {
        log.info("- 워라밸 만족도 계산 시작 - empNo={}", emp.getEmpNo());

        int wlbScore = 15;

        // 1. 초과 근무 (최대 -10점)
        wlbScore += wlbRetentionService.getOvertimeScore(emp.getEmpId(), year, month);

        // 2. 재택 근무 (최대 +2점)
        wlbScore += wlbRetentionService.getRemoteWorkScore(emp.getEmpId(), year, month);

        // 3. 자가 진단
        wlbScore += evaluationScoreService.getAdjustedScoreForForm(12, emp.getEmpId(), 3.0, year, month);
        wlbScore += evaluationScoreService.getAdjustedScoreForForm(10, emp.getEmpId(), 2.0, year, month);

        int finalScore = clampScore(wlbScore, 15);
        log.info("- 워라밸 만족도 계산 완료 - 결과={}", finalScore);

        return clampScore(wlbScore, 15);
    }

    /* 최대, 최소 점수 보장 */
    private int clampScore(int score, int max) {
        return Math.max(0, Math.min(score, max));
    }

    /* 나이 보정 계수  : 한국식 나이로 계산함 */
    private BigDecimal getAgeCoefficient(Integer year, Employee emp) {
        int birthYear = emp.getBirthDate().getYear();
        int age = year - birthYear + 1;

        if (age >= 60) {
            return BigDecimal.valueOf(1.3);
        } else if (age >= 50) {
            return BigDecimal.valueOf(1.1);
        } else if (age >= 40) {
            return BigDecimal.valueOf(0.85);
        } else if (age >= 30) {
            return BigDecimal.valueOf(1.0);
        } else {
            return BigDecimal.valueOf(1.3);
        }
    }

    /**
     * 총점 계산하는 로직
     */
    private BigDecimal weightedScore(
            int jobLevel, int compLevel, int relationLevel, int growthLevel,
            int tenureLevel, int wlbLevel, BigDecimal ageCoefficient
    ) {
        // 각 항목의 최대 점수
        final int MAX_JOB = 20, MAX_COMP = 20, MAX_REL = 15;
        final int MAX_GROW = 15, MAX_TENURE = 15, MAX_WLB = 15;

        // 감점 합산
        int jobPenalty = MAX_JOB - jobLevel;
        int compPenalty = MAX_COMP - compLevel;
        int relationPenalty = MAX_REL - relationLevel;
        int growthPenalty = MAX_GROW - growthLevel;
        int tenurePenalty = MAX_TENURE - tenureLevel;
        int wlbPenalty = MAX_WLB - wlbLevel;

        int totalPenalty = jobPenalty + compPenalty + relationPenalty + growthPenalty + tenurePenalty + wlbPenalty;

        // 감점 × 보정 계수
        BigDecimal deduction = BigDecimal.valueOf(totalPenalty)
                .multiply(ageCoefficient)
                .setScale(1, RoundingMode.HALF_UP);

        // 최종 점수
        BigDecimal finalScore = BigDecimal.valueOf(100)
                .subtract(deduction)
                .max(BigDecimal.ZERO)
                .min(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);

        // 로그 출력
        log.info("[총점 계산] 감점: 직무={}, 보상={}, 관계={}, 성장={}, 근속={}, 워라밸={}, 총감점={}, 보정계수={}, 최종점수={}",
                jobPenalty, compPenalty, relationPenalty, growthPenalty, tenurePenalty, wlbPenalty,
                totalPenalty, ageCoefficient, finalScore);

        return finalScore;
    }

}




