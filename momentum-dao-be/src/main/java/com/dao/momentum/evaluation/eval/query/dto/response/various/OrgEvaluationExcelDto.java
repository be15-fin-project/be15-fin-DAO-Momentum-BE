package com.dao.momentum.evaluation.eval.query.dto.response.various;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "조직 평가 엑셀 다운로드 DTO")
public record OrgEvaluationExcelDto(

    @Schema(description = "회차 번호", example = "2025-02")
    String roundNo,

    @Schema(description = "평가 양식명", example = "조직 몰입도")
    String formName,

    @Schema(description = "평가자 사번", example = "EMP2103")
    String evalEmpNo,

    @Schema(description = "평가자 이름", example = "정민우")
    String evalName,

    @Schema(description = "평가자 부서", example = "전략기획팀")
    String departmentName,

    @Schema(description = "평가자 직위", example = "차장")
    String positionName,

    @Schema(description = "평가 총점", example = "92")
    Integer score,

    @Schema(description = "제출일 (yyyy-MM-dd HH:mm)", example = "2025-06-20 09:15")
    String submittedAt

) {}
