package com.dao.momentum.evaluation.eval.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 요청 파라미터: 평가 양식(formId)과 페이징 정보
 */
@Getter
@Setter
@Schema(description = "평가 태스크 목록 조회 요청 DTO")
public class EvaluationTaskRequestDto {
    @Schema(description = "평가 양식 ID", example = "1")
    private int formId;

    @Schema(description = "회차 번호", example = "2")
    private int roundNo;

    @Schema(description = "페이지 번호 (1~)", example = "1")
    private int page = 1;

    @Schema(description = "페이지당 항목 수", example = "10")
    private int size = 20;

    /**
     * MyBatis OFFSET 계산
     */
    public int getOffset() {
        return (Math.max(page, 1) - 1) * size;
    }
}