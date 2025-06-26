package com.dao.momentum.organization.department.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DepartmentDeleteResponse {
    private Integer deptId;
}
