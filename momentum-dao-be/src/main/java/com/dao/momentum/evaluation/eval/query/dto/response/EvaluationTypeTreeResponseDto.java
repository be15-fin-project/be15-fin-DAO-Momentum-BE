package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "평가 타입 트리 응답 DTO")
public class EvaluationTypeTreeResponseDto {

    @Schema(description = "평가 타입 ID", example = "1")
    private final Long typeId;

    @Schema(description = "평가 타입 이름", example = "PEER")
    private final String typeName;

    @Schema(description = "평가 타입 설명", example = "사원 간 평가")
    private final String description;

    @Builder.Default
    @Schema(description = "소속 평가 양식 목록")
    private final List<EvaluationFormDto> children = List.of();
}
