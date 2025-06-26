package com.dao.momentum.organization.department.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.organization.department.command.application.dto.request.DepartmentCreateRequest;
import com.dao.momentum.organization.department.command.application.dto.request.DepartmentUpdateRequest;
import com.dao.momentum.organization.department.command.application.dto.response.DepartmentCreateResponse;
import com.dao.momentum.organization.department.command.application.dto.response.DepartmentDeleteResponse;
import com.dao.momentum.organization.department.command.application.dto.response.DepartmentUpdateResponse;
import com.dao.momentum.organization.department.command.application.service.DepartmentCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Delete;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/departments")
@Tag(name = "부서", description = "부서 관련 Command API")
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

    @Operation(summary="부서 수정", description = "관리자는 회사의 부서를 수정할 수 있다.")
    @PutMapping
    public ResponseEntity<ApiResponse<DepartmentUpdateResponse>> updateDepartment(
            @RequestBody @Valid DepartmentUpdateRequest request
    ){
        DepartmentUpdateResponse response = departmentCommandService.updateDepartment(request);

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @Operation(summary="부서 삭제", description = "관리자는 회사의 부서를 삭제할 수 있다.")
    @DeleteMapping("/{deptId}")
    public ResponseEntity<ApiResponse<DepartmentDeleteResponse>> deleteDepartment(
           @PathVariable Integer deptId
    ){
        DepartmentDeleteResponse response = departmentCommandService.deleteDepartment(deptId);

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }
}
