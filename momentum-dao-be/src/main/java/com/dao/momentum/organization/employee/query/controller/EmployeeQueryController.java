package com.dao.momentum.organization.employee.query.controller;

import com.dao.momentum.organization.employee.query.service.EmployeeQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class EmployeeQueryController {
    private final EmployeeQueryService employeeService;
}
