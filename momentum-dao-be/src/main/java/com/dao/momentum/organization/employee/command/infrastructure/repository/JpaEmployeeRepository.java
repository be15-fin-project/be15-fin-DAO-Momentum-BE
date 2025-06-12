package com.dao.momentum.organization.employee.command.infrastructure.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaEmployeeRepository extends EmployeeRepository, JpaRepository<Employee, Integer> {
}
