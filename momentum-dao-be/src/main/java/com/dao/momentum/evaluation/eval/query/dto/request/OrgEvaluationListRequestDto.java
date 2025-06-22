package com.dao.momentum.evaluation.eval.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Schema(description = "조직 평가 내역 조회 요청 DTO")
public class OrgEvaluationListRequestDto {

    @Schema(description = "평가 양식 ID (formId)", example = "5")
    private Integer formId;

    @Schema(description = "평가 회차 ID (roundId)", example = "2")
    private Integer roundId;

    @Schema(description = "페이지 번호", example = "1", defaultValue = "1")
    private Integer page = 1;

    @Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
    private Integer size = 10;

    public int getOffset() {
        return (page - 1) * size;
    }
}
