package com.dao.momentum.evaluation.kpi.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiStatisticsRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.request.KpiTimeseriesRequestDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiStatisticsResponseDto;
import com.dao.momentum.evaluation.kpi.query.dto.response.KpiTimeseriesResponseDto;
import com.dao.momentum.evaluation.kpi.query.service.KpiStatisticsService;
import com.dao.momentum.organization.employee.command.domain.repository.EmployeeRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kpi")
@Tag(name = "KPI 통계", description = "KPI 통계 관련 API")
public class KpiStatisticsController {

    private final KpiStatisticsService kpiStatisticsService;

    @GetMapping("/statistics")
    @Operation(
            summary = "KPI 통계 조회",
            description = "특정 연월/부서/사원 기준으로 KPI 통계를 조회합니다 (총 KPI 수, 완료 수, 평균 진척률)."
    )
    public ApiResponse<KpiStatisticsResponseDto> getKpiStatistics(
            @AuthenticationPrincipal UserDetails user,
            @ModelAttribute KpiStatisticsRequestDto requestDto
    ) {
        Long empId = Long.parseLong(user.getUsername());
        KpiStatisticsResponseDto result = kpiStatisticsService.getStatisticsWithAccessControl(requestDto, empId);
        return ApiResponse.success(result);
    }

    @GetMapping("/timeseries")
    @Operation(
            summary = "KPI 시계열 통계 조회",
            description = "연도별 월간 KPI 작성 수, 완료 수, 평균 진척률을 시계열로 조회합니다."
    )
    public ApiResponse<KpiTimeseriesResponseDto> getTimeseriesStatistics(
            @AuthenticationPrincipal UserDetails user,
            @ModelAttribute KpiTimeseriesRequestDto requestDto
    ) {
        Long empId = Long.parseLong(user.getUsername());
        KpiTimeseriesResponseDto result = kpiStatisticsService.getTimeseriesWithAccessControl(requestDto, empId);
        return ApiResponse.success(result);
    }
    @GetMapping("/my-statistics")
    @Operation(
            summary = "자신의 KPI 통계 조회",
            description = "특정 연월/부서/사원 기준으로 KPI 통계를 조회합니다 (총 KPI 수, 완료 수, 평균 진척률)."
    )
    public ApiResponse<KpiStatisticsResponseDto> getMyKpiStatistics(
            @AuthenticationPrincipal UserDetails user,
            @ModelAttribute KpiStatisticsRequestDto requestDto
    ) {
        Long empId = Long.parseLong(user.getUsername());
        KpiStatisticsResponseDto result = kpiStatisticsService.getStatisticsWithControl(requestDto, empId);
        return ApiResponse.success(result);
    }

    @GetMapping("/my-timeseries")
    @Operation(
            summary = "자신의 KPI 시계열 통계 조회",
            description = "연도별 월간 KPI 작성 수, 완료 수, 평균 진척률을 시계열로 조회합니다."
    )
    public ApiResponse<KpiTimeseriesResponseDto> getMyTimeseriesStatistics(
            @AuthenticationPrincipal UserDetails user,
            @ModelAttribute KpiTimeseriesRequestDto requestDto
    ) {
        Long empId = Long.parseLong(user.getUsername());
        KpiTimeseriesResponseDto result = kpiStatisticsService.getTimeseriesWithControl(requestDto, empId);
        return ApiResponse.success(result);
    }
}
