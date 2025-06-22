package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "요인별 점수 DTO")
public class FactorScoreDto {

    @Schema(description = "요인명", example = "커뮤니케이션")
    private String propertyName;

    @Schema(description = "점수", example = "17")
    private Integer score;
}
