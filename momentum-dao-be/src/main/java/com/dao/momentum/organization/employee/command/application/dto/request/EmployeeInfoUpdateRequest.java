package com.dao.momentum.organization.employee.command.application.dto.request;

import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmployeeInfoUpdateRequest {
    private String empNo;

    private String email;

    private Status status;
}
