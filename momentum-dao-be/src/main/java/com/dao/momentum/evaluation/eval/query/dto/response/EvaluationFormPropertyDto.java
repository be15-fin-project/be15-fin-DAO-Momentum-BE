package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "평가 양식별 요인 DTO")
public class EvaluationFormPropertyDto {
    @Schema(description = "요인 ID", example = "1")
    private Long propertyId;

    @Schema(description = "요인 이름", example = "의사소통 역량")
    private String name;

}
