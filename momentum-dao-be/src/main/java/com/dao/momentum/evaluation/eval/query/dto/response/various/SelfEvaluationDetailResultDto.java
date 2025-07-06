package com.dao.momentum.evaluation.eval.query.dto.response.various;

import com.dao.momentum.evaluation.eval.query.dto.request.FactorScoreDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "자가 진단 평가 상세 응답 DTO")
@Builder
public record SelfEvaluationDetailResultDto(

        @Schema(description = "기본 평가 정보")
        SelfEvaluationResponseDto detail,

        @Schema(description = "요인별 점수 목록")
        List<FactorScoreDto> factorScores
) { }
