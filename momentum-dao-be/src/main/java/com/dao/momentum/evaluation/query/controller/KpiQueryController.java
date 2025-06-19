package com.dao.momentum.evaluation.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiDetailResponseDto;
import com.dao.momentum.evaluation.query.dto.response.KpiListResultDto;
import com.dao.momentum.evaluation.query.service.KpiQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kpi")
@Tag(name = "KPI 조회", description = "KPI 관련 조회 API")
public class KpiQueryController {

    private final KpiQueryService kpiQueryService;

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
    public ApiResponse<KpiListResultDto> getKpiList(@ModelAttribute KpiListRequestDto requestDto) {
        return ApiResponse.success(kpiQueryService.getKpiList(requestDto));
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
}
