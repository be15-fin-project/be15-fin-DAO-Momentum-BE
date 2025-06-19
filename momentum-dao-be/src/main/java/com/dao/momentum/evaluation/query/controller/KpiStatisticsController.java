package com.dao.momentum.evaluation.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.query.dto.response.KpiTimeseriesResponseDto;
import com.dao.momentum.evaluation.query.service.KpiStatisticsService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kpi")
@Tag(name = "KPI 통계", description = "KPI 통계 관련 API")
public class KpiStatisticsController {

    private final KpiStatisticsService kpiStatisticsService;

    /**
     * KPI 통계 조회 (단일 시점)
     * - 총 KPI 수, 완료 수, 평균 진척률
     * - year, month, deptId, empId 필터 사용
     */
    @GetMapping("/statistics")
    @Operation(
            summary = "KPI 통계 조회",
            description = "특정 연월/부서/사원 기준으로 KPI 통계를 조회합니다 (총 KPI 수, 완료 수, 평균 진척률)."
    )
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
    @Operation(
            summary = "KPI 시계열 통계 조회",
            description = "연도별 월간 KPI 작성 수, 완료 수, 평균 진척률을 시계열로 조회합니다. year 미입력 시 현재 연도 기준입니다."
    )
    public ApiResponse<KpiTimeseriesResponseDto> getTimeseriesStatistics(
            @RequestParam(value = "year", required = false) Integer year) {
        KpiTimeseriesResponseDto result = kpiStatisticsService.getTimeseriesStatistics(year);
        return ApiResponse.success(result);
    }
}
