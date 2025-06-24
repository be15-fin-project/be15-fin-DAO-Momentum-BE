package com.dao.momentum.retention.query.dto.response;

import com.dao.momentum.retention.command.domain.aggregate.StabilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class RetentionGroupStatisticsDto {

    @Schema(description = "분류명 (예: 인사팀 / 대리)", example = "인사팀")
    private String groupName;

    @Schema(description = "평균 근속 지수", example = "74.2")
    private double averageScore;

    @Schema(description = "안정성 유형 분포")
    private Map<StabilityType, Double> stabilityRatio;
}
