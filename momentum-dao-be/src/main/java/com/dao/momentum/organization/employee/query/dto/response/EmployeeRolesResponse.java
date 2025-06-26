package com.dao.momentum.organization.employee.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EmployeeRolesResponse {
    private List<Integer> userRolesIds;
}
