package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사내 등급 비율 정보 DTO")
public class RateInfo {

    @Schema(description = "S등급 비율 (%)", example = "15")
    private int rateS;

    @Schema(description = "A등급 비율 (%)", example = "25")
    private int rateA;

    @Schema(description = "B등급 비율 (%)", example = "30")
    private int rateB;

    @Schema(description = "C등급 비율 (%)", example = "20")
    private int rateC;

    @Schema(description = "D등급 비율 (%)", example = "10")
    private int rateD;
}
