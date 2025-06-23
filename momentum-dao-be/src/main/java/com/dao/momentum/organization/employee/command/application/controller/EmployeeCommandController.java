package com.dao.momentum.organization.employee.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.common.exception.ErrorCode;
import com.dao.momentum.organization.employee.command.application.dto.request.AppointCreateRequest;
import com.dao.momentum.organization.employee.command.application.dto.request.EmployeeInfoUpdateRequest;
import com.dao.momentum.organization.employee.command.application.dto.request.EmployeeRecordsUpdateRequest;
import com.dao.momentum.organization.employee.command.application.dto.request.EmployeeRegisterRequest;
import com.dao.momentum.organization.employee.command.application.dto.response.AppointCreateResponse;
import com.dao.momentum.organization.employee.command.application.dto.response.EmployeeInfoUpdateResponse;
import com.dao.momentum.organization.employee.command.application.dto.response.EmployeeRecordsUpdateResponse;
import com.dao.momentum.organization.employee.command.application.service.EmployeeCommandService;
import com.dao.momentum.organization.position.exception.PositionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/employees")
@Tag(name = "사원 관리", description = "사원 정보 등록, 수정, 삭제 API")
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
            @PathVariable long empId,
            @RequestBody EmployeeInfoUpdateRequest request){

        return ResponseEntity.ok(
                ApiResponse.success(
                        employeeService.updateEmployeeInfo(userDetails, empId, request)
                )
        );
    }

    @Operation(summary = "사원 인사 정보 수정", description = "관리자는 사원의 인사 정보를 수정할 수 있다.")
    @PutMapping("/{empId}/hr-info")
    public ResponseEntity<ApiResponse<EmployeeRecordsUpdateResponse>> updateEmployeeRecords(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable long empId,
            @RequestBody EmployeeRecordsUpdateRequest request){

        return ResponseEntity.ok(
                ApiResponse.success(
                        employeeService.updateEmployeeRecords(userDetails, empId, request)
                )
        );
    }

    @Operation(summary = "사원 발령 등록", description = "관리자는 사원의 발령 정보를 등록할 수 있다.")
    @PostMapping("/appoint")
    public ResponseEntity<ApiResponse<AppointCreateResponse>> createAppoint(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody AppointCreateRequest request){

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        employeeService.createAppoint(userDetails, request)
                )
        );
    }


}
