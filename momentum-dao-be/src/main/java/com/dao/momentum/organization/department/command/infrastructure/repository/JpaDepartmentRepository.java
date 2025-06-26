package com.dao.momentum.organization.department.command.infrastructure.repository;

import com.dao.momentum.organization.department.command.domain.aggregate.Department;
import com.dao.momentum.organization.department.command.domain.repository.DepartmentRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaDepartmentRepository extends DepartmentRepository, JpaRepository<Department, Integer> {


    @Query("""
        SELECT d FROM Department d
        WHERE d.isDeleted = 'N'
        AND d.deptId NOT IN (
            SELECT DISTINCT sub.parentDeptId
            FROM Department sub
            WHERE sub.parentDeptId IS NOT NULL
        )
    """)
    List<Department> findActiveLeafDepartments();
}
