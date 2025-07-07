package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "평가 문항 Raw DTO (평가 양식 상세 조회용)")
@Builder
public record EvalFormPromptRaw(

        @Schema(description = "평가 양식명", example = "조직 몰입도")
        String formName,

        @Schema(description = "요인 ID", example = "101")
        Integer propertyId,

        @Schema(description = "요인 이름", example = "조직 몰입")
        String propertyName,

        @Schema(description = "문항 ID", example = "301")
        Integer promptId,

        @Schema(description = "문항 내용", example = "회사의 비전에 공감하며 일하고 있다.")
        String content,

        @Schema(description = "긍정 문항 여부", example = "true")
        Boolean isPositive
) { }
