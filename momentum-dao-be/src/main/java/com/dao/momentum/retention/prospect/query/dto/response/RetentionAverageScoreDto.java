package com.dao.momentum.retention.prospect.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RetentionAverageScoreDto {

    @Schema(description = "평균 점수", example = "72.6")
    private Double averageScore;
}
