package com.dao.momentum.retention.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class RetentionAverageScoreDto {

    @Schema(description = "평균 점수", example = "72.6")
    private Double averageScore;
}
