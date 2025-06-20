package com.dao.momentum.organization.employee.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.organization.employee.command.application.dto.request.EmployeeInfoUpdateRequest;
import com.dao.momentum.organization.employee.command.application.dto.request.EmployeeRegisterRequest;
import com.dao.momentum.organization.employee.command.application.dto.response.EmployeeInfoUpdateResponse;
import com.dao.momentum.organization.employee.command.application.service.EmployeeCommandService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/employees")
public class EmployeeCommandController {
    private final EmployeeCommandService employeeService;

    @Operation(summary = "사원 등록", description = "관리자는 사원의 정보를 입력하여 사원을 등록할 수 있다.")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createEmployee(@RequestBody @Valid EmployeeRegisterRequest request){
        //관리자 권한 검사 로그인 기능 구현 이후 추후 추가
        employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }

    @Operation(summary = "사원 기본 정보 수정", description = "관리자는 사원의 사번, 재직 상태, 이메일을 수정할 수 있다.")
    @PutMapping("/{empId}")
    public ResponseEntity<ApiResponse<EmployeeInfoUpdateResponse>> updateEmployeeInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long empId,
            @RequestBody EmployeeInfoUpdateRequest request){

        return ResponseEntity.ok(
                ApiResponse.success(
                        employeeService.updateEmployeeInfo(userDetails, empId, request)
                )
        );
    }
}
