package com.dao.momentum.evaluation.eval.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "평가 양식 속성 조회 요청 DTO")
@Builder
public record EvaluationFormPropertyRequestDto(

        @Schema(description = "조회할 평가 양식 ID", required = true, example = "4")
        Long formId
) { }
