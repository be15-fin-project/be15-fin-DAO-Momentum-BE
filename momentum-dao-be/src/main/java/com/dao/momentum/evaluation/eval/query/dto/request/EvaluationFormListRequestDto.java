package com.dao.momentum.evaluation.eval.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "평가 양식 목록 조회 요청 DTO")
@Builder
public record EvaluationFormListRequestDto(

        @Schema(description = "평가 유형 ID", example = "1", nullable = true)
        Integer typeId
) { }
