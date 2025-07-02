package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "평가 타입 정보 DTO")
public record EvaluationTypeDto(
    @Schema(description = "평가 타입 ID", example = "1")
    Long typeId,

    @Schema(description = "평가 타입 이름", example = "PEER")
    String typeName,

    @Schema(description = "평가 타입 설명", example = "사원 간 평가")
    String description
) {}
