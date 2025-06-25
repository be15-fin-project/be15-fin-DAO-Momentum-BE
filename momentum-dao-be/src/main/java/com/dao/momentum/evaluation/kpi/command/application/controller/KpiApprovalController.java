package com.dao.momentum.evaluation.kpi.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiApprovalRequest;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCancelApprovalRequest;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiApprovalResponse;
import com.dao.momentum.evaluation.kpi.command.application.service.KpiApprovalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kpis")
@RequiredArgsConstructor
@Tag(name = "KPI", description = "KPI 제출 및 취소에 대한 승인/반려 처리 API")
public class KpiApprovalController {

    private final KpiApprovalService kpiApprovalService;

    @PatchMapping("/{kpiId}/approval")
    @Operation(summary = "KPI 작성 승인/반려", description = "팀장이 KPI 작성 요청을 승인하거나 반려 처리합니다. 반려 시 사유 입력이 필요합니다.")
    public ApiResponse<KpiApprovalResponse> approveKpi(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long kpiId,
            @RequestBody @Valid KpiApprovalRequest request
    ) {
        Long managerId = Long.parseLong(userDetails.getUsername()); // 인증된 사용자 ID
        KpiApprovalResponse response = kpiApprovalService.approveKpi(managerId, kpiId, request);
        return ApiResponse.success(response);
    }

    @PatchMapping("/{kpiId}/cancel/approval")
    @Operation(summary = "KPI 취소 승인/반려", description = "팀장이 KPI 취소 요청에 대해 승인 또는 반려 처리합니다.")
    public ApiResponse<KpiApprovalResponse> approveCancelKpi(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long kpiId,
            @RequestBody @Valid KpiCancelApprovalRequest request
    ) {
        Long managerId = Long.parseLong(userDetails.getUsername());
        KpiApprovalResponse response = kpiApprovalService.approveCancelRequest(managerId, kpiId, request);
        return ApiResponse.success(response);
    }
}
