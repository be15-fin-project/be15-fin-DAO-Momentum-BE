package com.dao.momentum.evaluation.eval.query.dto.response.various;

import com.dao.momentum.evaluation.eval.query.dto.request.FactorScoreDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "사원 간 평가 상세 조회 결과 DTO")
@Builder
public record PeerEvaluationDetailResultDto(

        @Schema(description = "기본 평가 정보")
        PeerEvaluationResponseDto detail,

        @Schema(description = "요인별 점수 목록")
        List<FactorScoreDto> factorScores
) { }
