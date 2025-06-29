package com.dao.momentum.evaluation.kpi.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiEmployeeSummaryRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiDetailResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiEmployeeSummaryResultDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiListResultDto;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import com.dao.momentum.evaluation.kpi.query.service.KpiQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/kpi")
@Tag(name = "KPI 조회", description = "KPI 관련 조회 API")
public class KpiQueryController {

    private final KpiQueryService kpiQueryService;
    private final EmployeeRepository employeeRepository;

    @GetMapping("/list")
    @Operation(
            summary = "KPI 전체 내역 조회",
            description = "사번, 부서 ID, 직위 ID, 상태 ID, 작성일 범위로 KPI를 조회합니다. 페이징 지원."
    )
    public ApiResponse<KpiListResultDto> getKpiList(
            @AuthenticationPrincipal UserDetails user,
            @ModelAttribute KpiListRequestDto request
    ) {
        Long empId = Long.parseLong(user.getUsername());

        // 사번 조회 (builder 처리를 서비스로 넘기되, empNo만 컨트롤러에서 보완해도 됨)
        String empNo = employeeRepository.findByEmpId(empId)
                .orElseThrow(() -> new RuntimeException("사원 정보를 찾을 수 없습니다."))
                .getEmpNo();

        return ApiResponse.success(kpiQueryService.getKpiListWithAccessControl(request, empId, empNo));
    }

    @GetMapping("/{kpiId}")
    @Operation(
            summary = "KPI 세부 조회",
            description = "KPI ID로 KPI 상세 정보를 조회합니다. 목표, 진척 기준, 작성자 정보, 부서/직위, 기간 등을 포함합니다."
    )
    public ApiResponse<KpiDetailResponseDto> getKpiDetail(@PathVariable Long kpiId) {
        KpiDetailResponseDto result = kpiQueryService.getKpiDetail(kpiId);
        return ApiResponse.success(result);
    }

    @GetMapping("/employee-summary")
    @Operation(
            summary = "사원별 KPI 진척 현황 조회",
            description = "부서, 연도, 월 기준으로 사원별 KPI 통계를 조회합니다. 평균 진척도, 완료 KPI 수, 완료율 등을 반환합니다."
    )
    public ApiResponse<KpiEmployeeSummaryResultDto> getEmployeeKpiSummaries(
            @ModelAttribute KpiEmployeeSummaryRequestDto requestDto
    ) {
        KpiEmployeeSummaryResultDto result = kpiQueryService.getEmployeeKpiSummaries(requestDto);
        return ApiResponse.success(result);
    }

}
