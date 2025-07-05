package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "인사 평가 이의제기 상세 조회 응답 DTO")
public record ObjectionDetailResultDto(
        @Schema(description = "인사 평가 이의 제기 상세 정보")
        ObjectionItemDto itemDto,

        @Schema(description = "등급 비율 정보")
        RateInfo rateInfo,

        @Schema(description = "요인별 가중치 정보")
        WeightInfo weightInfo,

        @Schema(description = "요인별 점수 목록")
        List<FactorScoreDto> factorScores
) {
}
