package com.dao.momentum.organization.employee.query.service;

import com.dao.momentum.organization.department.exception.DepartmentException;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.department.command.domain.repository.DepartmentRepository;
import com.dao.momentum.organization.department.command.domain.repository.DeptHeadRepository;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ManagerFinderService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeRolesRepository employeeRolesRepository;
    private final DeptHeadRepository deptHeadRepository;
    private final DepartmentRepository departmentRepository;

    public Optional<Long> findManagerIdForEmp(Employee emp) {
        Long empId = emp.getEmpId();
        Integer deptId = emp.getDeptId();

        boolean isTeamLeader = employeeRolesRepository
                .findTeamLeadersByDeptId(deptId)
                .contains(empId);

        boolean isDeptHead = deptHeadRepository.findByEmpId(empId).isPresent();

        // 1. 일반 사원 → 팀장 반환
        if (!isTeamLeader && !isDeptHead) {
            return employeeRolesRepository.findTeamLeadersByDeptId(deptId).stream()
                    .filter(id -> !id.equals(empId)) // 혹시나 본인이 포함된 경우 제거
                    .findFirst();
        }

        // 2. 팀장 (부서장 아님) → 부서장 반환
        if (isTeamLeader && !isDeptHead) {
            return deptHeadRepository.findByDeptId(deptId)
                    .map(dh -> dh.getEmpId())
                    .filter(id -> !id.equals(empId));
        }

        // 3. 팀장 + 부서장 → 상위 부서장 반환
        if (isTeamLeader && isDeptHead) {
            Optional<Integer> parentDeptIdOpt = departmentRepository.findParentDeptIdByDeptId(deptId);

            if (parentDeptIdOpt.isEmpty()) {
                return Optional.empty(); // 상위 부서가 없음
            }

            Integer parentDeptId = parentDeptIdOpt.get();

            return deptHeadRepository.findByDeptId(parentDeptId)
                    .map(dh -> dh.getEmpId())
                    .filter(id -> !id.equals(empId)); // 자기 자신 제외
        }

        // 판단 불가한 경우
        return Optional.empty();
    }

}
