package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "평가 항목별 가중치 정보 DTO")
public class WeightInfo {

    @Schema(description = "업무 수행 역량 (%)", example = "25")
    private int weightPerform;

    @Schema(description = "협업 역량 (%)", example = "20")
    private int weightTeam;

    @Schema(description = "자기관리 및 태도 (%)", example = "15")
    private int weightAttitude;

    @Schema(description = "성장 가능성 (%)", example = "10")
    private int weightGrowth;

    @Schema(description = "조직 몰입도 (%)", example = "15")
    private int weightEngagement;

    @Schema(description = "최종 성과 (%)", example = "15")
    private int weightResult;
}
