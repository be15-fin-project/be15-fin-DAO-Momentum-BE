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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kpi")
@Tag(name = "KPI 조회", description = "KPI 관련 조회 API")
public class KpiQueryController {

    private final KpiQueryService kpiQueryService;
    private final EmployeeRepository employeeRepository;

    /**
     * KPI 전체 내역 조회
     * - 사번, 부서, 직위, 상태, 작성일 기간 기준 필터링
     * - 페이징 지원
     */
    @GetMapping("/list")
    @Operation(
            summary = "KPI 전체 내역 조회",
            description = "사번, 부서 ID, 직위 ID, 상태 ID, 작성일 범위로 KPI를 조회합니다. 페이징 지원."
    )
    public ApiResponse<KpiListResultDto> getKpiList(@ModelAttribute KpiListRequestDto request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String empIdStr = auth.getName(); // JWT subject = emp_id (Long)

        boolean isPrivileged = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> List.of("MASTER", "HR_MANAGER", "BOOKKEEPING", "MANAGER").contains(role));

        if (!isPrivileged) {
            Long empId = Long.parseLong(empIdStr);
            // 사번 조회
            String empNo = employeeRepository.findByEmpId(empId)
                    .orElseThrow(() -> new RuntimeException("사원 정보를 찾을 수 없습니다."))
                    .getEmpNo();

            request.setEmpNo(empNo);
        }

        return ApiResponse.success(kpiQueryService.getKpiList(request));
    }

    /**
     * KPI 세부 조회
     * - 관리자 및 인사팀은 KPI 목록에서 특정 항목 클릭 시 세부 정보 조회
     * - 반환 정보: 목표, 진척 기준, 목표 수치, 작성자, 부서/직위, 기간 등
     */
    @GetMapping("/{kpiId}")
    @Operation(
            summary = "KPI 세부 조회",
            description = "KPI ID로 KPI 상세 정보를 조회합니다. 목표, 진척 기준, 작성자 정보, 부서/직위, 기간 등을 포함합니다."
    )
    public ApiResponse<KpiDetailResponseDto> getKpiDetail(@PathVariable Long kpiId) {
        KpiDetailResponseDto result = kpiQueryService.getKpiDetail(kpiId);
        return ApiResponse.success(result);
    }

    /**
     * 사원별 KPI 진척 현황 조회
     * - 관리자/인사팀이 부서 및 기간 필터로 사원 KPI 요약 통계 조회
     */
    @GetMapping("/employee-summary")
    @Operation(
            summary = "사원별 KPI 진척 현황 조회",
            description = "부서, 연도, 월 기준으로 사원별 KPI 통계를 조회합니다. 평균 진척도, 완료 KPI 수, 완료율 등을 반환합니다."
    )
    public ApiResponse<KpiEmployeeSummaryResultDto> getEmployeeKpiSummaries(
            @ParameterObject @ModelAttribute KpiEmployeeSummaryRequestDto requestDto
    ) {
        KpiEmployeeSummaryResultDto result = kpiQueryService.getEmployeeKpiSummaries(requestDto);
        return ApiResponse.success(result);
    }
}
