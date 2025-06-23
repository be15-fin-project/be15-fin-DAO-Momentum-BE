package com.dao.momentum.retention.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "근속 전망 통계 응답 DTO")
public class RetentionStatisticsResponseDto {

    @Schema(description = "전체 평균 근속 지수", example = "78.6")
    private final double averageRetentionScore;

    @Schema(description = "부서별 안정성 유형 분포 리스트")
    private final List<StabilityDistributionByDeptDto> stabilityDistribution;
}
