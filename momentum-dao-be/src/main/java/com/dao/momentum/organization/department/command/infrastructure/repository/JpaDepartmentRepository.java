package com.dao.momentum.organization.department.command.infrastructure.repository;

import com.dao.momentum.organization.department.command.domain.aggregate.Department;
import com.dao.momentum.organization.department.command.domain.repository.DepartmentRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaDepartmentRepository extends DepartmentRepository, JpaRepository<Department, Integer> {
}
