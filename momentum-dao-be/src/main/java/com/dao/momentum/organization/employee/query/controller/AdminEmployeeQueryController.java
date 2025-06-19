package com.dao.momentum.organization.employee.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.organization.employee.query.dto.request.EmployeeDetailsRequest;
import com.dao.momentum.organization.employee.query.dto.request.EmployeeSearchRequest;
import com.dao.momentum.organization.employee.query.dto.response.EmployeeDetailsResponse;
import com.dao.momentum.organization.employee.query.dto.response.EmployeeListResponse;
import com.dao.momentum.organization.employee.query.service.AdminEmployeeQueryService;
import com.dao.momentum.work.query.dto.request.WorkSearchRequest;
import com.dao.momentum.work.query.dto.response.WorkListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Tag(name = "사원 정보 조회", description = "관리자 사원 정보 조회 API")
public class AdminEmployeeQueryController {
    private final AdminEmployeeQueryService adminEmployeeQueryService;

    @GetMapping
    @Operation(summary = "사원 전체 목록 조회", description = "관리자가 회사의 사원 전체 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<EmployeeListResponse>> getEmployees(
            @ModelAttribute EmployeeSearchRequest employeeSearchRequest
    ) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        adminEmployeeQueryService.getEmployees(employeeSearchRequest)
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
                        adminEmployeeQueryService.getEmployeeDetails(empId)
                )
        );
    }
}
