package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "인사 평가 이의제기 상세 조회 응답 DTO")
public class ObjectionDetailResultDto {

    // 인사 평가 결과 상세 정보
    @Schema(description = "인사 평가 이의 제기 상세 정보")
    private ObjectionItemDto itemDto;

    @Schema(description = "등급 비율 정보")
    private RateInfo rateInfo;

    @Schema(description = "요인별 가중치 정보")
    private WeightInfo weightInfo;

    @Schema(description = "요인별 점수 목록")
    private List<FactorScoreDto> factorScores;

}
