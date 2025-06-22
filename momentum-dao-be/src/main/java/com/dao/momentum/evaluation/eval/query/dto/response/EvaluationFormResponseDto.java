package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "평가 양식 응답 DTO")
public class EvaluationFormResponseDto {

    @Schema(description = "평가 양식 ID", example = "1")
    private int formId;

    @Schema(description = "평가 양식 이름", example = "PEER_REVIEW")
    private String name;

    @Schema(description = "평가 설명", example = "동료 평가")
    private String description;

    @Schema(description = "평가 유형 ID", example = "1")
    private int typeId;

    @Schema(description = "평가 유형 이름", example = "PEER")
    private String typeName;
}
