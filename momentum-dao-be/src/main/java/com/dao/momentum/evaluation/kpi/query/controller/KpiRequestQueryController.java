package com.dao.momentum.evaluation.kpi.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiRequestListRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiRequestListResultDto;
import com.dao.momentum.evaluation.kpi.query.service.KpiRequestQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kpi")
@Tag(name = "KPI 요청 목록", description = "팀장이 확인하는 KPI 요청 목록 API")
public class KpiRequestQueryController {

    private final KpiRequestQueryService kpiRequestQueryService;

    @GetMapping("/requests")
    @Operation(
            summary = "KPI 요청 목록 조회",
            description = "부서 내 구성원의 KPI 요청 목록을 조회합니다."
    )
    public ApiResponse<KpiRequestListResultDto> getKpiRequests(
            @AuthenticationPrincipal UserDetails user,
            @ModelAttribute KpiRequestListRequestDto requestDto
    ) {
        Long empId = Long.parseLong(user.getUsername());

        KpiRequestListResultDto result = kpiRequestQueryService.getKpiRequests(empId, requestDto);
        return ApiResponse.success(result);
    }
}
