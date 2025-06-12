package com.dao.momentum.organization.employee.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.organization.employee.command.application.dto.request.EmployeeRegisterRequest;
import com.dao.momentum.organization.employee.command.application.service.EmployeeCommandService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
}
