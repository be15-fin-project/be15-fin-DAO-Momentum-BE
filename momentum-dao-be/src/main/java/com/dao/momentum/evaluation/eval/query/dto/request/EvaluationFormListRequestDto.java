package com.dao.momentum.evaluation.eval.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "평가 양식 목록 조회 요청 DTO")
public class EvaluationFormListRequestDto {

    @Schema(description = "평가 유형 ID", example = "1", nullable = true)
    private Integer typeId;
}
