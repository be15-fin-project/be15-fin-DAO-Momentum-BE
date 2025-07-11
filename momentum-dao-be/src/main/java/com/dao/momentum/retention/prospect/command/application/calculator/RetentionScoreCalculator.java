package com.dao.momentum.retention.prospect.command.application.calculator;

import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.retention.prospect.command.application.dto.request.RetentionSupportDto;

public interface RetentionScoreCalculator {
    RetentionSupportDto calculate(Integer year, Integer month, Employee employee);
}
