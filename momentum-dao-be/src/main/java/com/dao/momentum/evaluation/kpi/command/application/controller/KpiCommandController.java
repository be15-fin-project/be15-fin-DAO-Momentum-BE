package com.dao.momentum.evaluation.kpi.command.application.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCreateDTO;
import com.dao.momentum.evaluation.kpi.command.application.dto.request.KpiCreateRequest;
import com.dao.momentum.evaluation.kpi.command.application.dto.response.KpiCreateResponse;
import com.dao.momentum.evaluation.kpi.command.application.service.KpiCommandService;
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
@Tag(name = "KPI", description = "KPI 생성 API")
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

}
