package com.dao.momentum.retention.prospect.query.controller;

import com.dao.momentum.common.dto.ApiResponse;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionInsightRequestDto;
import com.dao.momentum.retention.prospect.query.dto.request.RetentionStatisticsRequestDto;
import com.dao.momentum.retention.prospect.query.dto.response.RetentionAverageScoreDto;
import com.dao.momentum.retention.prospect.query.dto.response.StabilityDistributionByDeptDto;
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

    @GetMapping("/average-score")
    @Operation(summary = "전체 사원 평균 근속 지수 조회")
    public ApiResponse<RetentionAverageScoreDto> getAverageScore(
            @ModelAttribute RetentionStatisticsRequestDto req
    ) {
        return ApiResponse.success(service.getAverageScore(req));
    }

    @GetMapping("/stability-distribution")
    @Operation(summary = "부서별 근속 안정성 유형 분포 조회")
    public ApiResponse<List<StabilityDistributionByDeptDto>> getStabilityDistributionByDept(
            @ModelAttribute RetentionInsightRequestDto req
    ) {
        List<StabilityDistributionByDeptDto> result = service.getStabilityDistributionByDept(req);
        return ApiResponse.success(result);
    }

    @GetMapping("/stability-distribution/overall")
    @Operation(summary = "전체 근속 안정성 유형 비율 조회 (부서 필터 가능)")
    public ApiResponse<StabilityDistributionByDeptDto> getOverallStabilityDistribution(
            @ModelAttribute RetentionInsightRequestDto req
    ) {
        StabilityDistributionByDeptDto result = service.getOverallStabilityDistribution(req);
        return ApiResponse.success(result);
    }

}
