package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@Schema(description = "사원 간 평가 상세 조회 결과 DTO")
public class PeerEvaluationDetailResultDto {

    @Schema(description = "기본 평가 정보")
    private PeerEvaluationDetailResponseDto detail;

    @Schema(description = "요인별 점수 목록")
    private List<FactorScoreDto> factorScores;
}
