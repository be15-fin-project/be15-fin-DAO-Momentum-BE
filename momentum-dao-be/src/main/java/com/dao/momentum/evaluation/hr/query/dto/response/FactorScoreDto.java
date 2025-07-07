package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "요인별 점수 DTO")
public record FactorScoreDto(
        @Schema(description = "요인 ID", example = "11")
        String propertyId,

        @Schema(description = "요인명", example = "커뮤니케이션")
        String propertyName,

        @Schema(description = "점수 등급", example = "S")
        String score
) {
}
