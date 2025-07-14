package com.dao.momentum.evaluation.eval.query.dto.request.various;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "자가 진단 평가 엑셀 다운로드 요청 DTO")
@Builder
public record SelfEvaluationExcelRequestDto(

        @Schema(description = "회차 ID", example = "3")
        Long roundId,

        @Schema(description = "평가 양식 ID", example = "5")
        Integer formId,

        @Schema(description = "사번", example = "20240001")
        String empNo,

        @Schema(description = "부서 ID", example = "10")
        Long deptId,

        @Schema(description = "직위 ID", example = "5")
        Long positionId,

        @Schema(description = "제출 여부", example = "true")
        Boolean submitted
) { }
