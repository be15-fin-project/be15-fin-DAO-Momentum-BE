package com.dao.momentum.evaluation.hr.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "HR 평가 등급 생성 DTO")
public record HrRateCreateDTO(
        @Schema(description = "S등급 비율", example = "50")
        int rateS,

        @Schema(description = "A등급 비율", example = "30")
        int rateA,

        @Schema(description = "B등급 비율", example = "15")
        int rateB,

        @Schema(description = "C등급 비율", example = "5")
        int rateC,

        @Schema(description = "D등급 비율", example = "0")
        int rateD
) {
}
