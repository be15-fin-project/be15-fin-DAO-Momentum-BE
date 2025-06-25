package com.dao.momentum.organization.department.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.organization.department.query.dto.response.DepartmentsInfoResponse;
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
}
