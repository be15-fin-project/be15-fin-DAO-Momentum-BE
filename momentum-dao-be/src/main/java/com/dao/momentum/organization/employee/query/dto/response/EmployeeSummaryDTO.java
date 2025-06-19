package com.dao.momentum.organization.employee.query.dto.response;

import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
import com.dao.momentum.organization.employee.command.domain.aggregate.UserRoleName;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Getter
@Builder
public class EmployeeSummaryDTO {
    private long empId;

    private String empNo;

    private String name;

    private Integer deptId;

    private String deptName;

    private Integer positionId;

    private String positionName;

    private String userRolesInString;

    private LocalDate joinDate;

    private Status status;

    public List<UserRoleName> getUserRoles() {
        if (userRolesInString == null) {
            return List.of(UserRoleName.EMPLOYEE);
        }

        return Arrays.stream(userRolesInString.split(","))
                .map(UserRoleName::fromString)
                .toList();
    }
}
