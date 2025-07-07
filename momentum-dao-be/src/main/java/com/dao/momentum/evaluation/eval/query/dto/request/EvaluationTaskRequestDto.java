package com.dao.momentum.evaluation.eval.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "평가 태스크 목록 조회 요청 DTO")
@Builder
public record EvaluationTaskRequestDto(

        @Schema(description = "평가 유형 ID", example = "3")
        Integer typeId,

        @Schema(description = "평가 양식 ID", example = "1")
        Integer formId,

        @Schema(description = "회차 번호", example = "2")
        Integer roundNo,

        @Schema(description = "페이지 번호 (1~)", example = "1")
        Integer page,

        @Schema(description = "페이지당 항목 수", example = "10")
        Integer size
) {

    
    public EvaluationTaskRequestDto {
        if (page == null) { 
            page = 1;
        }
        if (size == null) { // Default value is 10 if not provided
            size = 10;
        }
        if (typeId == null) typeId = 0;
        if (formId == null) formId = 0;
    }

    
    public int getOffset() {
        return (page - 1) * size;
    }
}
