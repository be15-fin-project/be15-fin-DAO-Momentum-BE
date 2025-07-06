package com.dao.momentum.evaluation.eval.query.dto.request.various;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "사원 간 평가 엑셀 다운로드 요청 DTO")
@Builder
public record PeerEvaluationExcelRequestDto(

        @Schema(description = "회차 ID", example = "3")
        Long roundId,

        @Schema(description = "평가 대상자 사번", example = "20240001")
        String targetEmpNo,

        @Schema(description = "평가자 사번", example = "20250001")
        String evaluatorEmpNo,

        @Schema(description = "부서 ID", example = "10")
        Long deptId,

        @Schema(description = "직위 ID", example = "5")
        Long positionId
) { }
