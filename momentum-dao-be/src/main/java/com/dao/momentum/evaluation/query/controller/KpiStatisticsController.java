package com.dao.momentum.evaluation.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.query.dto.response.KpiTimeseriesResponseDto;
import com.dao.momentum.evaluation.query.service.KpiStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kpi")
public class KpiStatisticsController {

    private final KpiStatisticsService kpiStatisticsService;

    /**
     * KPI 통계 조회 (단일 시점)
     * - 총 KPI 수, 완료 수, 평균 진척률
     * - year, month, deptId, empId 필터 사용
     */
    @GetMapping("/statistics")
    public ApiResponse<KpiStatisticsResponseDto> getKpiStatistics(
            @ModelAttribute KpiStatisticsRequestDto requestDto
    ) {
        KpiStatisticsResponseDto result = kpiStatisticsService.getStatistics(requestDto);
        return ApiResponse.success(result);
    }

    /**
     * KPI 시계열 통계 조회
     * - 연도별 월별 KPI 작성 수, 완료 수, 평균 진척률
     * - year 파라미터 없으면 현재 연도 기준
     */
    @GetMapping("/timeseries")
    public ApiResponse<KpiTimeseriesResponseDto> getTimeseriesStatistics(
            @RequestParam(value = "year", required = false) Integer year) {
        KpiTimeseriesResponseDto result = kpiStatisticsService.getTimeseriesStatistics(year);
        return ApiResponse.success(result);
    }
}
