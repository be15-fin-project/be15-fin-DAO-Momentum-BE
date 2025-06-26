package com.dao.momentum.organization.employee.command.domain.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.EmployeeRoles;

public interface EmployeeRolesRepository {
    <S extends EmployeeRoles> S save(EmployeeRoles employeeRoles);
}
