package com.dao.momentum.organization.department.command.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class DepartmentUpdateResponse {
    private DepartmentUpdateDTO departmentUpdateDTO;
}
