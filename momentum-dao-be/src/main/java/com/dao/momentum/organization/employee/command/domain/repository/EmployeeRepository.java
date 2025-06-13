package com.dao.momentum.organization.employee.command.domain.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.employee.Employee;

import java.util.Optional;

public interface EmployeeRepository {
    <S extends Employee> S save(Employee employee);

    Optional<Employee> findByEmail(String Email);

    Optional<Employee> findByEmpId(Long empId);

    String findMaxEmpNo();
}
