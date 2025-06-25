package com.dao.momentum.organization.employee.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EmployeeCSVResponse {
    private List<Long> empIds;

    private String message;
}
