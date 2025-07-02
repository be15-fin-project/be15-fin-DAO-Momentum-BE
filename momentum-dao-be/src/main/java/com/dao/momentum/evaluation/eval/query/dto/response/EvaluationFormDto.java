package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "평가 양식 정보 DTO")
public record EvaluationFormDto( 
    @Schema(description = "양식 ID", example = "1")
    Long formId,

    @Schema(description = "양식 이름", example = "동료 평가")
    String formName,

    @Schema(description = "양식 설명", example = "같은 부서 동료 대상 평가")
    String description,

    @Schema(description = "평가 타입 ID", example = "1")
    Long typeId
) {}
