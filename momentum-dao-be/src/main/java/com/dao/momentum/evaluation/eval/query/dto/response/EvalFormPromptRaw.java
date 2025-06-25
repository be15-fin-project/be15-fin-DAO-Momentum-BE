package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "평가 문항 Raw DTO (평가 양식 상세 조회용)")
public class EvalFormPromptRaw {

    @Schema(description = "평가 양식명", example = "조직 몰입도")
    private String formName;

    @Schema(description = "요인 ID", example = "101")
    private Integer propertyId;

    @Schema(description = "요인 이름", example = "조직 몰입")
    private String propertyName;

    @Schema(description = "문항 ID", example = "301")
    private Integer promptId;

    @Schema(description = "문항 내용", example = "회사의 비전에 공감하며 일하고 있다.")
    private String content;

    @Schema(description = "긍정 문항 여부", example = "true")
    private Boolean isPositive;
}
