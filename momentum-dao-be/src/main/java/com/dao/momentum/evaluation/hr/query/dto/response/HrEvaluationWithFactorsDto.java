package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "사원 인사평가 내역 + 요인 점수 포함 DTO")
public record HrEvaluationWithFactorsDto(

        @Schema(description = "기본 인사 평가 정보")
        HrEvaluationItemDto item,

        @Schema(description = "요인별 점수 목록")
        List<FactorScoreDto> factorScores

) {
}
