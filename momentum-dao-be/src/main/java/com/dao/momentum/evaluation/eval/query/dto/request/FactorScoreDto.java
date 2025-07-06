package com.dao.momentum.evaluation.eval.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "요인별 점수 DTO")
@Builder
public record FactorScoreDto(

        @Schema(description = "요인명", example = "커뮤니케이션")
        String propertyName,

        @Schema(description = "점수", example = "17")
        Integer score
) { }
