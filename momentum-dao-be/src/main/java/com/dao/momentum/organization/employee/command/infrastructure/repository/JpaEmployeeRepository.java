package com.dao.momentum.organization.employee.command.infrastructure.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface JpaEmployeeRepository extends EmployeeRepository, JpaRepository<Employee, Long> {
    <S extends Employee> S save(Employee employee);

    Optional<Employee> findByEmail(String email);

    Optional<Employee> findByEmpId(Long empId);

    @Query(value = "SELECT emp_no FROM employee " +
            "WHERE CHAR_LENGTH(emp_no) = 8 AND emp_no REGEXP '^[0-9]{8}$' " +
            "ORDER BY CAST(emp_no AS UNSIGNED) DESC LIMIT 1",
            nativeQuery = true)
    String findMaxEmpNo();

    Boolean existsByPositionId(Integer positionId);


    // 근속 전망 확인을 위한 현 재직 중인 사원 목록 조회
    List<Employee> findByStatus(String status); // Spring Data JPA 규칙 기반


}
