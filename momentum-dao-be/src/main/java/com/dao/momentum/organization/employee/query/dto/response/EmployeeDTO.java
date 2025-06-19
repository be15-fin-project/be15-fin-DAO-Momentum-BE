package com.dao.momentum.organization.employee.query.dto.response;

import com.dao.momentum.organization.employee.command.domain.aggregate.Gender;
import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
import com.dao.momentum.organization.employee.command.domain.aggregate.UserRoleName;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Getter
@Builder
public class EmployeeDTO {
    private long empId;

    private String empNo;

    private String name;

    private String email;

    private Integer deptId;

    private String deptName;

    private Integer positionId;

    private String positionName;

    private String userRolesInString;

    private Gender gender;

    private String address;

    private String contact;

    private LocalDate joinDate;

    private Status status;

//    private Integer remainingDayoffHours;

//    private Integer remainingRefreshDays;

    private LocalDate birthDate;

    public List<UserRoleName> getUserRoles() {
        if (userRolesInString == null) {
            return List.of(UserRoleName.EMPLOYEE);
        }

        return Arrays.stream(userRolesInString.split(","))
                .map(UserRoleName::fromString)
                .toList();
    }
}
