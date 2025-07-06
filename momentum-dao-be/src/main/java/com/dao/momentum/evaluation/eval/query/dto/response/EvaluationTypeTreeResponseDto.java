package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "평가 타입 트리 응답 DTO")
@Builder
public record EvaluationTypeTreeResponseDto(

        @Schema(description = "평가 타입 ID", example = "1")
        Long typeId,

        @Schema(description = "평가 타입 이름", example = "PEER")
        String typeName,

        @Schema(description = "평가 타입 설명", example = "사원 간 평가")
        String description,

        @Schema(description = "소속 평가 양식 목록")
        List<EvaluationFormDto> children
) {
    public EvaluationTypeTreeResponseDto {
        if (children == null) {
            children = List.of(); // Assign default empty list if null
        }
    }
}
