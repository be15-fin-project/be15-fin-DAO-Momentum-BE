package com.dao.momentum.evaluation.eval.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SelfEvaluationDetailResultDto {

    private final SelfEvaluationResponseDto detail;
    private final List<FactorScoreDto> factorScores;
}
