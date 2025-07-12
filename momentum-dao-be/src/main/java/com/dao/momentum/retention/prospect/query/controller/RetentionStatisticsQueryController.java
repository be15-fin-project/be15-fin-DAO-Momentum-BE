package com.dao.momentum.retention.prospect.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionInsightRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionStatisticsRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionTimeseriesRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.StabilityRatioByDeptRaw;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionAverageScoreDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionMonthlyStatDto;
import com.dao.momentum.retention.prospect.query.dto.response.StabilityRatioByDeptDto;
import com.dao.momentum.retention.prospect.query.dto.response.StabilityRatioSummaryDto;
import com.dao.momentum.retention.prospect.query.service.RetentionStatisticsQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/retention/statistics")
@RequiredArgsConstructor
@Tag(name = "근속통계", description = "근속 지수 및 안정성 통계 API")
public class RetentionStatisticsQueryController {

    private final RetentionStatisticsQueryService service;

    @GetMapping("/overview")
    @Operation(summary = "평균 근속 지수, 사원 수, 안정형/위험군 비율을 요약 조회")
    public ApiResponse<RetentionAverageScoreDto> getAverageScore(
            @ModelAttribute RetentionStatisticsRequestDto req
    ) {
        return ApiResponse.success(service.getAverageScore(req));
    }

    @GetMapping("/stability-distribution")
    @Operation(summary = "부서별 근속 안정성 구간 분포 조회")
    public ApiResponse<List<StabilityRatioByDeptDto>> getStabilityDistributionByDept(
            @ModelAttribute RetentionInsightRequestDto req
    ) {
        List<StabilityRatioByDeptDto> result = service.getStabilityDistributionByDept(req);
        return ApiResponse.success(result);
    }

    @GetMapping("/stability-distribution/overall")
    @Operation(summary = "전체 근속 안정성 유형 비율 조회 (부서 필터 가능)")
    public ApiResponse<StabilityRatioSummaryDto> getOverallStabilityDistribution(
            @ModelAttribute RetentionInsightRequestDto req
    ) {
        StabilityRatioSummaryDto result = service.getOverallStabilityDistribution(req);
        return ApiResponse.success(result);
    }

    @GetMapping("/timeseries")
    @Operation(summary = "월별 평균 근속 지수 및 표준편차 시계열 조회")
    public ApiResponse<List<RetentionMonthlyStatDto>> getRetentionTimeseriesStats(
            @ModelAttribute RetentionTimeseriesRequestDto req
    ) {
        return ApiResponse.success(service.getMonthlyRetentionStats(req));
    }
}
