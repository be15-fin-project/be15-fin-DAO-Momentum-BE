package com.dao.momentum.evaluation.kpi.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiRequestListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiRequestListResultDto;
import com.dao.momentum.evaluation.kpi.query.service.KpiRequestQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kpi/requests")
@Tag(name = "KPI 요청 목록", description = "팀장이 확인하는 KPI 요청 목록 API")
public class KpiRequestQueryController {

    private final KpiRequestQueryService kpiRequestQueryService;

    @GetMapping
    @Operation(
            summary = "KPI 요청 목록 조회",
            description = """
            팀장이 본인 부서 내 구성원의 KPI 요청 현황을 확인합니다.
            - 팀장+부서장: 부서 전체 요청 가능
            - 팀장만: 부서 내 본인 제외
            - 부서장만: 팀장만 조회 가능
            """
    )
    public ApiResponse<KpiRequestListResultDto> getKpiRequests(
            @ModelAttribute KpiRequestListRequestDto requestDto
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long requesterEmpId = Long.valueOf(auth.getName()); // JWT subject: empId

        KpiRequestListResultDto result = kpiRequestQueryService.getKpiRequests(requesterEmpId, requestDto);
        return ApiResponse.success(result);
    }
}
