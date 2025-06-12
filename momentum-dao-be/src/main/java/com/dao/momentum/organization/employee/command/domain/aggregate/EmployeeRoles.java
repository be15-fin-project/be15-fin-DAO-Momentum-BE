package com.dao.momentum.organization.employee.command.domain.aggregate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRoles {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer employeeRolesId;

    private Integer empId;

    private Integer UserRoleId;
}
