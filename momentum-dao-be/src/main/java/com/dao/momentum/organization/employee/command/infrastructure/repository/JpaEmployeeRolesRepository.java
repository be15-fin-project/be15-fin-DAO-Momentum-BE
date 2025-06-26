package com.dao.momentum.organization.employee.command.infrastructure.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.EmployeeRoles;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRolesRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaEmployeeRolesRepository extends EmployeeRolesRepository, JpaRepository<EmployeeRoles, Integer> {
    <S extends EmployeeRoles> S save(EmployeeRoles employeeRoles);
}
