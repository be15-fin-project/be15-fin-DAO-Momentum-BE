package com.dao.momentum.retention.prospect.command.application.calculator;

import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.retention.prospect.command.application.dto.request.RetentionSupportDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Component
public class RetentionScoreCalculatorImpl implements RetentionScoreCalculator {

    @Override
    public RetentionSupportDto calculate(Employee employee) {
        int jobLevel = 80;
        int compLevel = 75;
        int relationLevel = 70;
        int growthLevel = 65;
        BigDecimal tenureLevel = calculateTenureYears(employee);
        int wlbLevel = 85;

        int retentionScore = weightedScore(
                jobLevel, compLevel, relationLevel, growthLevel, tenureLevel, wlbLevel
        );

        return new RetentionSupportDto(
                jobLevel, compLevel, relationLevel, growthLevel, tenureLevel, wlbLevel, retentionScore
        );
    }

    private BigDecimal calculateTenureYears(Employee employee) {
        long days = ChronoUnit.DAYS.between(employee.getJoinDate(), LocalDate.now());
        return BigDecimal.valueOf(days).divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
    }

    private int weightedScore(int job, int comp, int relation, int growth, BigDecimal tenure, int wlb) {
        double score = job * 0.25 +
                comp * 0.20 +
                relation * 0.15 +
                growth * 0.15 +
                tenure.doubleValue() * 0.15 * 20 +
                wlb * 0.10;
        return (int) Math.round(score);
    }
}
