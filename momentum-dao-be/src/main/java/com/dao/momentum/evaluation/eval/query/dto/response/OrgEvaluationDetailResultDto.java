package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "조직 평가 상세 응답 DTO")
public class OrgEvaluationDetailResultDto {

    @Schema(description = "기본 평가 정보")
    private OrgEvaluationResponseDto detail;

    @Schema(description = "요인별 점수 목록")
    private List<FactorScoreDto> factorScores;

}
