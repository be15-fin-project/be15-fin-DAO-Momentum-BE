package com.dao.momentum.organization.employee.command.application.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoleUpdateRequest {
    private long empId;

    private List<Integer> userRoleIds;

}
