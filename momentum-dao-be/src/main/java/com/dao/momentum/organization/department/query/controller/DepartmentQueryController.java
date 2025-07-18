package com.dao.momentum.organization.department.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.organization.department.query.dto.response.DepartmentDetailResponse;
import com.dao.momentum.organization.department.query.dto.response.DepartmentTreeResponse;
import com.dao.momentum.organization.department.query.dto.response.DepartmentsInfoResponse;
import com.dao.momentum.organization.department.query.dto.response.LeafDepartmentResponse;
import com.dao.momentum.organization.department.query.service.DepartmentQueryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/departments")
@RequiredArgsConstructor
public class DepartmentQueryController {
    private final DepartmentQueryService departmentQueryService;

    @Operation(summary = "부서 정보 조회", description = "사원은 회사의 부서 목록을 조회할 수 있다.")
    @GetMapping
    public ResponseEntity<ApiResponse<DepartmentsInfoResponse>> getDepartmentsInfo(){
        DepartmentsInfoResponse response = departmentQueryService.getDepartmentsInfo();

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @Operation(summary = "부서 상세조회", description = "사원은 부서의 상세 정보를 조회할 수 있다.")
    @GetMapping("/{deptId}")
    public ResponseEntity<ApiResponse<DepartmentDetailResponse>> getDepartmentDetails(
            @PathVariable int deptId
    ){
        DepartmentDetailResponse response = departmentQueryService.getDepartmentDetails(deptId);

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @Operation(summary = "부서 목록 및 소속 사원 조회", description = "사원은 회사의 부서 목록 및 소속 사원을 조회할 수 있다.")
    @GetMapping("/tree-with-employees")
    public ResponseEntity<ApiResponse<DepartmentTreeResponse>> getDepartmentTreeWithEmployees(){
        DepartmentTreeResponse response = departmentQueryService.getDepartmentTreeWithEmployees();

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }

    @Operation(summary = "최하위 부서 목록 조회", description = "통계 자료를 위해 최하위 부서 목록을 조회한다.")
    @GetMapping("/leaf")
    public ResponseEntity<ApiResponse<LeafDepartmentResponse>> getLeafDepartments(){
        LeafDepartmentResponse response = departmentQueryService.getLeafDepartments();

        return ResponseEntity.ok().body(ApiResponse.success(response));
    }
}
