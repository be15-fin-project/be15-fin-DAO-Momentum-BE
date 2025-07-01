package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "평가 회차 요약 DTO")
public class EvaluationRoundSimpleDto {

    @Schema(description = "평가 회차 ID", example = "1")
    private Long roundId;

    @Schema(description = "평가 회차 번호 (ex: 2024-1차)", example = "2024-1차")
    private String roundNo;
}
