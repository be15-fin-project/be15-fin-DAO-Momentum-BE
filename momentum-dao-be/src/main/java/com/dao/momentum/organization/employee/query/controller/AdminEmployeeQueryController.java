package com.dao.momentum.organization.employee.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.organization.employee.query.dto.request.AppointSearchRequest;
import com.dao.momentum.organization.employee.query.dto.request.EmployeeSearchRequest;
import com.dao.momentum.organization.employee.query.dto.response.*;
import com.dao.momentum.organization.employee.query.service.EmployeeQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Tag(name = "사원 정보 조회", description = "관리자 사원 정보 조회 API")
public class AdminEmployeeQueryController {
    private final EmployeeQueryService employeeQueryService;

    @GetMapping
    @Operation(summary = "사원 전체 목록 조회", description = "관리자가 회사의 사원 전체 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<EmployeeListResponse>> getEmployees(
            @ModelAttribute EmployeeSearchRequest employeeSearchRequest
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        employeeQueryService.getEmployees(employeeSearchRequest)
                )
        );
    }

    @GetMapping("/{empId}")
    @Operation(summary = "사원 상세 조회", description = "관리자가 특정 사원 정보를 상세 조회합니다.")
    public ResponseEntity<ApiResponse<EmployeeDetailsResponse>> getEmployeeDetails(
            @PathVariable long empId
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        employeeQueryService.getEmployeeDetails(empId)
                )
        );
    }

    @GetMapping("/appoints")
    @Operation(summary = "사원 발령 내역 조회", description = "관리자가 사원 발령 내역을 조회합니다.")
    public ResponseEntity<ApiResponse<AppointListResponse>> getAppoints(
            @ModelAttribute AppointSearchRequest appointSearchRequest
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        employeeQueryService.getAppoints(appointSearchRequest)
                )
        );
    }

    @GetMapping("/roles")
    @Operation(summary = "권한 목록 조회", description = "관리자가 서비스 권한 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<UserRolesResponse>> getRoles(
    ) {
        UserRolesResponse response = employeeQueryService.getUserRoles();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{empId}/roles")
    @Operation(summary = "사원 권한 목록 조회", description = "관리자가 특정 사원의 서비스 권한 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<EmployeeRolesResponse>> getEmployeeRoles(
            @PathVariable long empId
    ) {
        EmployeeRolesResponse response = employeeQueryService.getEmployeeRoles(empId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
