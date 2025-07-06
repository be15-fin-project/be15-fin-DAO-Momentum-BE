package com.dao.momentum.evaluation.hr.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "HR 평가 가중치 업데이트 DTO")
public record HrWeightUpdateDTO(
        @Schema(description = "실적 가중치", example = "40")
        int performWt,

        @Schema(description = "팀워크 가중치", example = "20")
        int teamWt,

        @Schema(description = "태도 가중치", example = "10")
        int attitudeWt,

        @Schema(description = "성장 가중치", example = "15")
        int growthWt,

        @Schema(description = "몰입도 가중치", example = "10")
        int engagementWt,

        @Schema(description = "결과 가중치", example = "5")
        int resultWt
) {
}
