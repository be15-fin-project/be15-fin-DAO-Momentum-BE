package com.dao.momentum.retention.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.retention.query.dto.request.RetentionForecastRequestDto;
import com.dao.momentum.retention.query.dto.request.RetentionSupportDetailRequestDto;
import com.dao.momentum.retention.query.dto.response.RetentionForecastResponseDto;
import com.dao.momentum.retention.query.dto.response.RetentionSupportDetailDto;
import com.dao.momentum.retention.query.service.RetentionSupportQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/retention")
public class RetentionSupportQueryController {

    private final RetentionSupportQueryService service;

    @Operation(summary = "근속 전망 목록 조회", description = "근속 지수, 안정성 유형 등을 포함한 근속 전망 데이터를 회차별로 조회합니다.")
    @GetMapping("/forecast")
    public ApiResponse<RetentionForecastResponseDto> getForecasts(
            @Parameter(description = "조회 조건과 필터") RetentionForecastRequestDto request
    ) {
        RetentionForecastResponseDto result = service.getRetentionForecasts(request);
        return ApiResponse.success(result);
    }

    @GetMapping("/{retentionId}")
    @Operation(summary = "근속 전망 상세 조회", description = "근속 지원 ID로 상세 전망 정보를 조회")
    public ApiResponse<RetentionSupportDetailDto> getSupportDetail(
            @Parameter(description = "근속 지원 ID", required = true)
            @PathVariable Long retentionId
    ) {
        return ApiResponse.success(service.getSupportDetail(retentionId));
    }
}
