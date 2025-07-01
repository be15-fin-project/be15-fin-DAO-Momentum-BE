package com.dao.momentum.evaluation.kpi.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.*;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.CancelKpiResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiCreateResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiProgressUpdateResponse;
import com.dao.momentum.evaluation.kpi.command.application.service.KpiCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kpi")
@RequiredArgsConstructor
@Tag(name = "KPI", description = "KPI 생성, 취소 요청 API")
public class KpiCommandController {

    private final KpiCommandService kpiCommandService;

    @PostMapping
    @Operation(summary = "KPI 작성", description = "사원이 KPI를 작성하고 상급자에게 승인을 요청합니다.")
    public ApiResponse<KpiCreateResponse> createKpi(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody @Valid KpiCreateRequest request
    ) {
        Long empId = Long.parseLong(userDetails.getUsername());
        return ApiResponse.success(kpiCommandService.createKpi(empId, request.toDTO()));
    }

    @DeleteMapping("/{kpiId}")
    @Operation(summary = "KPI 취소 요청", description = "사원이 승인된 KPI에 대해 취소 요청을 합니다.")
    public ApiResponse<CancelKpiResponse> cancelKpi(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long kpiId,
            @RequestBody @Valid KpiCancelRequest request
    ) {
        Long empId = Long.parseLong(userDetails.getUsername());
        CancelKpiResponse response = kpiCommandService.cancelKpi(empId, kpiId, request.getReason());
        return ApiResponse.success(response);
    }

    @PatchMapping("/{kpiId}/progress")
    @Operation(summary = "KPI 진척도 업데이트", description = "사원이 KPI 항목의 진척도 수치를 입력하여 최신화합니다.")
    public ApiResponse<KpiProgressUpdateResponse> updateProgress(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long kpiId,
            @RequestBody @Valid KpiProgressUpdateRequest request
    ) {
        Long empId = Long.parseLong(userDetails.getUsername());
        KpiProgressUpdateResponse response = kpiCommandService.updateProgress(empId, kpiId, request);
        return ApiResponse.success(response);
    }
}
