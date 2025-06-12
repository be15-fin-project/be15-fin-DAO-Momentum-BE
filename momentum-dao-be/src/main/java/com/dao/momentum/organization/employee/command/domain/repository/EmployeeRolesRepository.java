package com.dao.momentum.organization.employee.command.domain.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.aggregate.EmployeeRoles;

import java.util.List;

public interface EmployeeRolesRepository {
    <S extends EmployeeRoles> S save(EmployeeRoles employeeRoles);
}
