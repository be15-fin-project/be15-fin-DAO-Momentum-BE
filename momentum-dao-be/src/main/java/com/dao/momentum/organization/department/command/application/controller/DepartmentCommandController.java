package com.dao.momentum.organization.department.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.organization.department.command.application.dto.request.DepartmentCreateRequest;
import com.dao.momentum.organization.department.command.application.dto.response.DepartmentCreateResponse;
import com.dao.momentum.organization.department.command.application.service.DepartmentCommandService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/departments")
public class DepartmentCommandController {
    private final DepartmentCommandService departmentCommandService;

    @Operation(summary="부서 등록", description = "관리자는 회사의 부서를 등록할 수 있다.")
    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentCreateResponse>> createDepartment(
            @RequestBody @Valid DepartmentCreateRequest request
    ){
        DepartmentCreateResponse response = departmentCommandService.createDepartment(request);

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }
}
