package com.dao.momentum.evaluation.eval.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "조직 평가 엑셀 다운로드 요청 DTO")
public class OrgEvaluationExcelRequestDto {

    @Schema(description = "회차 ID", example = "3")
    private Long roundId;

    @Schema(description = "평가자 사번", example = "20250001")
    private String evalEmpNo;

    @Schema(description = "부서 ID", example = "10")
    private Long deptId;

    @Schema(description = "직위 ID", example = "5")
    private Long positionId;

}
