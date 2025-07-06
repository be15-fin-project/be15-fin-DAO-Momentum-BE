package com.dao.momentum.evaluation.eval.query.dto.request.various;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "사원 간 평가 목록 조회 요청 DTO")
@Builder
public record PeerEvaluationListRequestDto(

        @Schema(description = "평가자 사번", example = "1001")
        String evalNo,

        @Schema(description = "부서 ID", example = "10")
        Long deptId,

        @Schema(description = "직위 ID", example = "5")
        Long positionId,

        @Schema(description = "피평가자 사번", example = "20250002")
        Long targetNo,

        @Schema(description = "평가 종류 ID", example = "3")
        Integer formId,

        @Schema(description = "평가 회차", example = "2")
        Integer roundNo,

        @Schema(description = "페이지 번호", example = "1", defaultValue = "1")
        Integer page,

        @Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
        Integer size
) {

    // 기본값 처리
    public PeerEvaluationListRequestDto {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
    }

    // 페이지 오프셋 계산 메서드
    public int getOffset() {
        return (page - 1) * size;
    }
}
