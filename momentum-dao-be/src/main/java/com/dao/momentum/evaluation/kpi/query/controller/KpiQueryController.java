package com.dao.momentum.evaluation.kpi.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiDashboardRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiEmployeeSummaryRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiDashboardResponseDto;
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

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/kpi")
@Tag(name = "KPI 조회", description = "KPI 관련 조회 API")
public class KpiQueryController {

    private final KpiQueryService kpiQueryService;

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

        return ApiResponse.success(kpiQueryService.getKpiListWithAccessControl(request, empId));
    }

    @GetMapping("/my-list")
    @Operation(
            summary = "자신의 KPI 전체 내역 조회",
            description = "사번, 부서 ID, 직위 ID, 상태 ID, 작성일 범위로 KPI를 조회합니다. 페이징 지원."
    )
    public ApiResponse<KpiListResultDto> getMyKpiList(
            @AuthenticationPrincipal UserDetails user,
            @ModelAttribute KpiListRequestDto request
    ) {
        Long empId = Long.parseLong(user.getUsername());

        return ApiResponse.success(kpiQueryService.getKpiListWithControl(request, empId));
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

    @GetMapping("/dashboard")
    @Operation(
            summary = "대시보드 KPI 조회",
            description = "자신의 KPI 중 지정된 날짜 범위 내 존재하는 KPI들을 조회합니다."
    )
    public ApiResponse<List<KpiDashboardResponseDto>> getDashboardKpis(
            @AuthenticationPrincipal UserDetails user,
            @ModelAttribute KpiDashboardRequestDto request
    ) {
        Long empId = Long.parseLong(user.getUsername());

        List<KpiDashboardResponseDto> result = kpiQueryService.getDashboardKpis(empId, request);
        return ApiResponse.success(result);
    }
}
