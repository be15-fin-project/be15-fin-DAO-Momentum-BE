package com.dao.momentum.organization.employee.command.application.service;

import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.department.command.domain.aggregate.Department;
import com.dao.momentum.organization.department.command.domain.repository.DepartmentRepository;
import com.dao.momentum.organization.department.exception.DepartmentException;
import com.dao.momentum.organization.employee.command.application.dto.request.AppointCreateRequest;
import com.dao.momentum.organization.employee.command.application.dto.response.AppointCreateResponse;
import com.dao.momentum.organization.employee.command.domain.aggregate.Appoint;
import com.dao.momentum.organization.employee.command.domain.aggregate.AppointType;
import com.dao.momentum.organization.employee.command.domain.aggregate.Employee;
import com.dao.momentum.organization.employee.command.domain.aggregate.Status;
import com.dao.momentum.organization.employee.command.domain.repository.AppointRepository;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.dao.momentum.organization.position.command.domain.aggregate.IsDeleted;
import com.dao.momentum.organization.position.command.domain.aggregate.Position;
import com.dao.momentum.organization.position.command.domain.repository.PositionRepository;
import com.dao.momentum.organization.position.exception.PositionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointCommandService {
    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;
    private final AppointRepository appointRepository;
    private final EmployeeRepository employeeRepository;

    private final EmployeeCommandService employeeCommandService;

    public AppointCreateResponse createAppoint(UserDetails userDetails, AppointCreateRequest request) {
        long adminId = Long.parseLong(userDetails.getUsername());
        employeeCommandService.validateActiveAdmin(adminId);

        long empId = request.getEmpId();
        int afterPositionId = request.getPositionId();
        int afterDeptId = request.getDeptId();

        Employee emp = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

        int beforePositionId = emp.getPositionId();
        Position beforePosition = positionRepository.findByPositionId(beforePositionId)
                .orElseThrow(() -> new PositionException(ErrorCode.POSITION_NOT_FOUND));

        Position afterPosition = positionRepository.findByPositionId(afterPositionId)
                .orElseThrow(() -> new PositionException(ErrorCode.POSITION_NOT_FOUND));

        int beforeDeptId = emp.getDeptId();
        Department afterDept = departmentRepository.findById(afterDeptId)
                .orElseThrow(() -> new DepartmentException(ErrorCode.DEPARTMENT_NOT_FOUND));

        AppointType type = request.getType();

        switch (type) {
            case PROMOTION -> validatePromotion(emp, beforePosition, afterPosition, afterDept);
            case DEPARTMENT_TRANSFER -> validateTransfer(emp, afterDept, afterPosition);
        }

        LocalDate appointDate = request.getAppointDate();
        LocalDate today = LocalDate.now();
        if (appointDate.isBefore(today)) {
            throw new EmployeeException(ErrorCode.INVALID_COMMAND_REQUEST);
        }

        Appoint appoint = Appoint.builder()
                .empId(empId)
                .beforePosition(beforePositionId)
                .afterPosition(afterPositionId)
                .beforeDepartment(beforeDeptId)
                .afterDepartment(afterDeptId)
                .type(type)
                .appointDate(appointDate)
                .build();

        appointRepository.save(appoint);
        long appointId = appoint.getAppointId();

        if (appointDate.equals(today)) { // 날짜가 오늘이면 바로 반영, 아니면 배치 처리
            emp.fromAppoint(afterDeptId, afterPositionId);
            employeeRepository.save(emp);
        }

        log.info("인사 발령 등록 성공 - 발령 ID: {}, 발령 등록자 ID: {}, 발령 대상자 ID: {}, 등록 일시: {}", appointId, adminId, empId, LocalDateTime.now());
        return AppointCreateResponse.builder()
                .appointId(appointId)
                .message("인사 발령 등록 성공")
                .build();
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정 배치작업
    public void applyAppoint() {
        LocalDateTime start = LocalDateTime.now();
        log.info("[발령 등록 Batch System] 발령 등록 시작");
        LocalDate today = LocalDate.now();
        long countApplied = 0;

        List<Appoint> pendingAppoints = appointRepository.findByAppointDateLessThanEqual(today);
        for (Appoint appoint : pendingAppoints) {
            long empId = appoint.getEmpId();
            Employee emp = employeeRepository.findByEmpId(empId)
                    .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

            boolean isApplied = emp.getDeptId() == appoint.getAfterDepartment() && emp.getPositionId() == appoint.getAfterPosition(); // 결과가 같다면 이미 처리 완료된 발령

            if (!isApplied) {
                countApplied++;
                emp.fromAppoint(appoint.getAfterDepartment(), appoint.getAfterPosition());
                employeeRepository.save(emp);
            }
        }
        LocalDateTime end = LocalDateTime.now();
        long duration = Duration.between(start, end).toSeconds();
        log.info("[발령 등록 Batch System] 발령 등록 완료 - {}명, 소요 시간 - {}초", countApplied, duration);
    }

    private void validatePromotion(Employee emp, Position beforePosition, Position afterPosition, Department afterDept) {
        int beforeLevel = beforePosition.getLevel();

        validateActivePosition(afterPosition);

        int afterLevel = afterPosition.getLevel();

        if (afterLevel != beforeLevel - 1) {
            throw new EmployeeException(ErrorCode.INVALID_POSITION_FOR_PROMOTION);
        }

        int beforeDeptId = emp.getDeptId();

        if (beforeDeptId != afterDept.getDeptId()) {
            throw new EmployeeException(ErrorCode.INVALID_DEPARTMENT_FOR_PROMOTION);
        }

    }

    private void validateTransfer(Employee emp, Department afterDept, Position afterPosition) {
        int beforeDeptId = emp.getDeptId();

        validateActivePosition(afterPosition);

        if (beforeDeptId == afterDept.getDeptId()) {
            throw new EmployeeException(ErrorCode.INVALID_DEPARTMENT_FOR_TRANSFER);
        }
    }

    private void validateActivePosition(Position position) {
        // soft delete 여부 확인
        if (position.getIsDeleted() == IsDeleted.Y) {
            throw new EmployeeException(ErrorCode.POSITION_NOT_FOUND);
        }
    }
}
