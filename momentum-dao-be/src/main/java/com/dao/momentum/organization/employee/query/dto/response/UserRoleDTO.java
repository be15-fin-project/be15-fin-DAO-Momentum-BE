package com.dao.momentum.organization.employee.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRoleDTO {
    private int userRoleId;

    private String userRoleName;
}
