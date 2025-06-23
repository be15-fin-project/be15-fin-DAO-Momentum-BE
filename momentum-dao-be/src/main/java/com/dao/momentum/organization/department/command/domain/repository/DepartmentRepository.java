package com.dao.momentum.organization.department.command.domain.repository;

import com.dao.momentum.organization.department.command.domain.aggregate.Department;

import java.util.Optional;

public interface DepartmentRepository {
    Optional<Department> findById(int deptId);
}
