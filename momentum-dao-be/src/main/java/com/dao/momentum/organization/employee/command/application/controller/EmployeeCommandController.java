package com.dao.momentum.organization.employee.command.application.controller;

import com.dao.momentum.organization.employee.command.application.service.EmployeeCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class EmployeeCommandController {
    private final EmployeeCommandService employeeService;
}
