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
import com.dao.momentum.organization.employee.command.domain.repository.AppointRepository;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.organization.employee.exception.EmployeeException;
import com.dao.momentum.organization.position.command.domain.aggregate.IsDeleted;
import com.dao.momentum.organization.position.command.domain.aggregate.Position;
import com.dao.momentum.organization.position.command.domain.repository.PositionRepository;
import com.dao.momentum.organization.position.exception.PositionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class AppointCommandService {
    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;
    private final AppointRepository appointRepository;
    private final EmployeeRepository employeeRepository;

    private final EmployeeCommandService employeeCommandService;

    @Transactional
    public AppointCreateResponse createAppoint(UserDetails userDetails, AppointCreateRequest request) {
        long adminId = Long.parseLong(userDetails.getUsername());
        employeeCommandService.validateActiveAdmin(adminId);

        long empId = request.getEmpId();
        Employee emp = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));
        int currentPositionId = emp.getPositionId();
        Integer currentDeptId = emp.getDeptId();

        LocalDate today = LocalDate.now();
        // 1) 이미 등록되었지만 아직 적용되지 않은 발령이 있으면 등록 차단 (발령일 설정 기능 제공 시 필요한 검증 로직)
//        List<Appoint> pendingAppoints = appointRepository.findAllPendingAppoints(empId, today, currentPositionId, currentDeptId);
//
//        if (!pendingAppoints.isEmpty()) {
//            throw new EmployeeException(ErrorCode.PENDING_APPOINT_EXISTS);
//        }

        int afterPositionId = request.getPositionId();
        Integer afterDeptId = request.getDeptId();

        Position beforePosition = positionRepository.findByPositionId(currentPositionId)
                .orElseThrow(() -> new PositionException(ErrorCode.POSITION_NOT_FOUND));

        Position afterPosition = positionRepository.findByPositionId(afterPositionId)
                .orElseThrow(() -> new PositionException(ErrorCode.POSITION_NOT_FOUND));

        Department afterDept = null;
        if (afterDeptId != null) {
            afterDept = departmentRepository.findById(afterDeptId)
                    .orElseThrow(() -> new DepartmentException(ErrorCode.DEPARTMENT_NOT_FOUND));
        }

        AppointType type = request.getType();

        switch (type) {
            case PROMOTION -> validatePromotion(emp, beforePosition, afterPosition, afterDept);
            case DEPARTMENT_TRANSFER -> validateTransfer(emp, afterDept, afterPosition);
        }

        LocalDate appointDate = today;

        /* 추후 제공 예정인 기능 */
//        LocalDate appointDate = request.getAppointDate();
//        if (appointDate.isBefore(today)) {
//            throw new EmployeeException(ErrorCode.INVALID_APPOINT_DATE);
//        }

        Appoint appoint = Appoint.builder()
                .empId(empId)
                .beforePosition(currentPositionId)
                .afterPosition(afterPositionId)
                .beforeDepartment(currentDeptId)
                .afterDepartment(afterDeptId)
                .type(type)
                .appointDate(appointDate)
                .build();

        appointRepository.save(appoint);
        long appointId = appoint.getAppointId();

//        if (appointDate.equals(today)) { // 날짜가 오늘이면 바로 반영, 아니면 배치 처리 (발령일 설정 기능 제공 시 if 블럭 복구)
            emp.fromAppoint(afterDeptId, afterPositionId);
            employeeRepository.save(emp);
//        }

        log.info("인사 발령 등록 성공 - 발령 ID: {}, 발령 등록자 ID: {}, 발령 대상자 ID: {}, 등록 일시: {}", appointId, adminId, empId, LocalDateTime.now());
        return AppointCreateResponse.builder()
                .appointId(appointId)
                .message("인사 발령 등록 성공")
                .build();
    }

    /* 추후 제공 예정인 기능 */
    /*
    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정 배치작업
    public void applyAppoint() {
        LocalDateTime start = LocalDateTime.now();
        log.info("[발령 등록 Batch System] 발령 등록 시작");
        LocalDate today = LocalDate.now();
        long countApplied = 0;

        List<Long> invalidAppointIds = new ArrayList<>();
        List<Appoint> pendingAppoints = appointRepository.findByAppointDateLessThanEqual(today);
        for (Appoint appoint : pendingAppoints) {
            long empId = appoint.getEmpId();
            Employee emp = employeeRepository.findByEmpId(empId)
                    .orElseThrow(() -> new EmployeeException(ErrorCode.EMPLOYEE_NOT_FOUND));

            // 2) 발령 테이블 before 정보와 현재 정보가 일치해야만 처리
            boolean isInvalidAppoint = !Objects.equals(appoint.getBeforeDepartment(), emp.getDeptId()) || appoint.getBeforePosition() != emp.getPositionId();

            if (isInvalidAppoint) {
                invalidAppointIds.add(appoint.getAppointId());
                continue;
            }

            boolean isApplied = Objects.equals(emp.getDeptId(), appoint.getAfterDepartment()) && emp.getPositionId() == appoint.getAfterPosition(); // 결과가 같다면 이미 처리 완료된 발령

            if (!isApplied) {
                countApplied++;
                emp.fromAppoint(appoint.getAfterDepartment(), appoint.getAfterPosition());
                employeeRepository.save(emp);
            }
        }
        LocalDateTime end = LocalDateTime.now();
        long duration = Duration.between(start, end).toSeconds();
        log.info("[발령 등록 Batch System] 발령 등록 완료 - {}명, 소요 시간 - {}초", countApplied, duration);
        if (!invalidAppointIds.isEmpty()) {
            log.warn("[발령 등록 Batch System] 처리되지 않은 유효하지 않은 발령 ID 목록: {}", invalidAppointIds);
        }
    }
*/
    private void validatePromotion(Employee emp, Position beforePosition, Position afterPosition, Department afterDept) {
        int beforeLevel = beforePosition.getLevel();

        validateActivePosition(afterPosition);

        int afterLevel = afterPosition.getLevel();

        if (afterLevel != beforeLevel - 1) {
            throw new EmployeeException(ErrorCode.INVALID_POSITION_FOR_PROMOTION);
        }

        Integer beforeDeptId = emp.getDeptId();
        Integer afterDeptId = (afterDept != null) ? afterDept.getDeptId() : null;

        if (!Objects.equals(beforeDeptId, afterDeptId)) {
            throw new EmployeeException(ErrorCode.INVALID_DEPARTMENT_FOR_PROMOTION);
        }
    }

    private void validateTransfer(Employee emp, Department afterDept, Position afterPosition) {
        Integer beforeDeptId = emp.getDeptId();
        Integer afterDeptId = null;
        if (afterDept != null) {
            afterDeptId = afterDept.getDeptId();
        }
        validateActivePosition(afterPosition);

        if (Objects.equals(beforeDeptId, afterDeptId)) {
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
