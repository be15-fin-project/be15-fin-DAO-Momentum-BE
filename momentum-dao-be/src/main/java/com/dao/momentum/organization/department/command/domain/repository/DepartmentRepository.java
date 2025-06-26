package com.dao.momentum.organization.department.command.domain.repository;

import com.dao.momentum.organization.department.command.domain.aggregate.Department;
import com.dao.momentum.organization.department.command.domain.aggregate.IsDeleted;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

public interface DepartmentRepository {
    Optional<Department> findById(int deptId);

    Optional<Department> findByNameAndIsDeleted(String deptName, IsDeleted isDeleted);

    Department save(Department department);

    Integer isSubDepartment(int parentDeptId,int childDeptId);

    boolean existsByDeptIdAndIsDeleted(Integer deptId, IsDeleted isDeleted);
}
