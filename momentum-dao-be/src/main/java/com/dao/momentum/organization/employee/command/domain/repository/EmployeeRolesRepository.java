package com.dao.momentum.organization.employee.command.domain.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.EmployeeRoles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRolesRepository {
    <S extends EmployeeRoles> S save(EmployeeRoles employeeRoles);

    void deleteAllByEmpId(long empId);

    // 팀장 여부 확인
    @Query(value = """
        SELECT CASE WHEN COUNT(*) > 0 THEN TRUE ELSE FALSE END
        FROM employee_roles
        WHERE emp_id = :empId
          AND user_role_id = :userRoleId
    """, nativeQuery = true)
    boolean existsManagerRole(@Param("empId") Long empId, @Param("userRoleId") Long userRoleId);

    // 팀장 여부 확인
    @Query(value = """
                SELECT COUNT(*)
                FROM employee_roles
                WHERE emp_id = :empId
                  AND user_role_id = :userRoleId
            """, nativeQuery = true)
    Integer countManagerRole(@Param("empId") Long empId, @Param("userRoleId") Long userRoleId);


    // 해당 부서의 팀장 ID 목록 조회
    @Query(value = """
        SELECT e.emp_id
        FROM employee e
        JOIN employee_roles er ON e.emp_id = er.emp_id
        WHERE e.dept_id = :deptId
          AND er.user_role_id = 4
    """, nativeQuery = true)
    List<Long> findTeamLeadersByDeptId(@Param("deptId") Integer deptId);


}
