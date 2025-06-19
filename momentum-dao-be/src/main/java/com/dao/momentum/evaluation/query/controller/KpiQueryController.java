package com.dao.momentum.evaluation.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.query.dto.request.KpiListRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiListResultDto;
import com.dao.momentum.evaluation.query.service.KpiQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kpi")
public class KpiQueryController {

    private final KpiQueryService kpiQueryService;

    /**
     * KPI 전체 내역 조회
     * - 사번, 부서, 직위, 상태, 작성일 기간 기준 필터링
     * - 페이징 지원
     */
    @GetMapping("/list")
    public ApiResponse<KpiListResultDto> getKpiList(@ModelAttribute KpiListRequestDto requestDto) {
        return ApiResponse.success(kpiQueryService.getKpiList(requestDto));
    }
}
