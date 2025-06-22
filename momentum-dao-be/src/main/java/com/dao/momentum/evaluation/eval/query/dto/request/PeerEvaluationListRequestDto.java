package com.dao.momentum.evaluation.eval.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Schema(description = "사원 간 평가 목록 조회 요청 DTO")
public class PeerEvaluationListRequestDto {

    @Schema(description = "평가자 사번", example = "1001")
    private Long evalId;

    @Schema(description = "피평가자 사번", example = "20250002")
    private Long targetId;

    @Schema(description = "평가 종류 ID (form ID)", example = "3")
    private Integer formId;

    @Schema(description = "평가 회차 ID (round ID)", example = "2")
    private Integer roundId;

    @Schema(description = "페이지 번호", example = "1", defaultValue = "1")
    private Integer page = 1;

    @Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
    private Integer size = 10;

    public int getOffset() {
        return (page - 1) * size;
    }
}
