package com.dao.momentum.organization.department.command.infrastructure.repository;

import com.dao.momentum.organization.department.command.domain.aggregate.Department;
import com.dao.momentum.organization.department.command.domain.aggregate.IsDeleted;
import com.dao.momentum.organization.department.command.domain.repository.DepartmentRepository;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface JpaDepartmentRepository extends DepartmentRepository, JpaRepository<Department, Integer> {
    @Query(value = """
    WITH RECURSIVE dept_tree AS (
        SELECT dept_id, parent_dept_id
        FROM department
        WHERE dept_id = :parentDeptId
        UNION ALL
        SELECT d.dept_id, d.parent_dept_id
        FROM department d
        INNER JOIN dept_tree dt ON d.parent_dept_id = dt.dept_id
    )
    SELECT EXISTS (
        SELECT 1 FROM dept_tree WHERE dept_id = :childDeptId
    )
    """, nativeQuery = true)
    Integer isSubDepartment(@Param("parentDeptId") int parentDeptId, @Param("childDeptId") int childDeptId);

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

    List<Department> findAllByIsDeleted(IsDeleted isDeleted);

    @Query("SELECT d.parentDeptId FROM Department d WHERE d.deptId = :deptId")
    Optional<Integer> findParentDeptIdByDeptId(@Param("deptId") Integer deptId);

}
