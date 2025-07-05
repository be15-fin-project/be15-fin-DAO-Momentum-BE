package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "평가 항목별 가중치 정보 DTO")
public record WeightInfo(
        @Schema(description = "업무 수행 역량 (%)", example = "25")
        int weightPerform,

        @Schema(description = "협업 역량 (%)", example = "20")
        int weightTeam,

        @Schema(description = "자기관리 및 태도 (%)", example = "15")
        int weightAttitude,

        @Schema(description = "성장 가능성 (%)", example = "10")
        int weightGrowth,

        @Schema(description = "조직 몰입도 (%)", example = "15")
        int weightEngagement,

        @Schema(description = "최종 성과 (%)", example = "15")
        int weightResult
) {
}
