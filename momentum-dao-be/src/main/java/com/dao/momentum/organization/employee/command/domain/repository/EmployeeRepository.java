package com.dao.momentum.organization.employee.command.domain.repository;

import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    <S extends Employee> S save(Employee employee);

    Optional<Employee> findByEmail(String Email);

    Optional<Employee> findByEmpId(Long empId);

    String findMaxEmpNo();

    Boolean existsByPositionId(Integer positionId);

    boolean existsByEmpId(Long empId);

    boolean existsByEmpIdAndDeptId(Long empId, Integer DeptId);

    List<Employee> findAllByStatus(Status status);

    // 근속 전망 확인을 위한 현 재직 중인 사원 목록 조회
    List<Employee> findByStatus(Status status);

    List<Employee> findAllById(Iterable<Long> ids);

    boolean existsByDeptIdAndStatusIsNot(Integer deptId,Status status);

    @Query("SELECT e.empNo FROM Employee e WHERE e.empId = :empId")
    String findEmpNoByEmpId(Long empId);
}
