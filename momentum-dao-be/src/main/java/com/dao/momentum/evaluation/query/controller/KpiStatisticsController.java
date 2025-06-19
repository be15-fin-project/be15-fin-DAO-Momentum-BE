package com.dao.momentum.evaluation.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.query.service.KpiStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kpi")
public class KpiStatisticsController {

    private final KpiStatisticsService kpiStatisticsService;

    @GetMapping("/statistics")
    public ApiResponse<KpiStatisticsResponseDto> getKpiStatistics(
            @ModelAttribute KpiStatisticsRequestDto requestDto
    ) {
        KpiStatisticsResponseDto result = kpiStatisticsService.getStatistics(requestDto);
        return ApiResponse.success(result);
    }
}
