package com.dao.momentum.organization.employee.command.application.dto.response;

import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmployeeInfoDTO {
    private Long empId;

    private String empNo;

    private String email;

    private Status status;

    public static EmployeeInfoDTO fromEmployee(Employee employee) {

        return EmployeeInfoDTO.builder()
                .empId(employee.getEmpId())
                .empNo(employee.getEmpNo())
                .email(employee.getEmail())
                .status(employee.getStatus())
                .build();
    }
}
