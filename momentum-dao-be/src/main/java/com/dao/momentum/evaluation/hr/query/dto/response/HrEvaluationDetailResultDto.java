package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "인사 평가 상세 응답 DTO")
public class HrEvaluationDetailResultDto {

    @Schema(description = "인사 평가 기본 정보")
    private HrEvaluationDetailDto content;

    @Schema(description = "등급 비율 정보")
    private RateInfo rateInfo;

    @Schema(description = "요인별 가중치 정보")
    private WeightInfo weightInfo;

    @Schema(description = "요인별 점수 목록")
    private List<FactorScoreDto> factorScores;
}

