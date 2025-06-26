package com.dao.momentum.organization.employee.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.organization.employee.query.dto.response.EmployeeDetailsResponse;
import com.dao.momentum.organization.employee.query.service.AdminEmployeeQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
@Tag(name = "사원 정보 조회", description = "사원 본인 정보 조회 API")
public class EmployeeQueryController {
    private final AdminEmployeeQueryService adminEmployeeQueryService;

    @GetMapping("/me")
    @Operation(summary = "사원 개인 정보 조회", description = "사원이 본인 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<EmployeeDetailsResponse>> getEmployeeDetails(
            @AuthenticationPrincipal UserDetails userDetails
            ) {
        long empId = Long.parseLong(userDetails.getUsername());

        return ResponseEntity.ok(
                ApiResponse.success(
                        adminEmployeeQueryService.getEmployeeDetails(empId)
                )
        );
    }
}
