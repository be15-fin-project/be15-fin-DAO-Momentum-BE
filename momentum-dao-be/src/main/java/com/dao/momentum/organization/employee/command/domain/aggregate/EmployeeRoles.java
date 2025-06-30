package com.dao.momentum.organization.employee.command.domain.aggregate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long employeeRolesId;

    private Long empId;

    private Integer UserRoleId;

    @Builder
    public EmployeeRoles(Long empId, Integer userRoleId) {
        this.empId = empId;
        UserRoleId = userRoleId;
    }
}
