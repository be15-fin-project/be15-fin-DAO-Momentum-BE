package com.dao.momentum.evaluation.eval.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "요인별 점수 DTO")
public record EvalFactorScoreDto(

        @Schema(description = "요인 ID", example = "101")
        @NotNull
        Integer propertyId,

        @Schema(description = "점수", example = "17")
        @NotNull
        Integer score
) { }
