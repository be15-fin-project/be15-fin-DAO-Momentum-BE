package com.dao.momentum.evaluation.eval.query.dto.response.various;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "다면 평가 엑셀 다운로드 DTO")
public record PeerEvaluationExcelDto(

    @Schema(description = "회차 번호", example = "2025-01")
    String roundNo,

    @Schema(description = "평가 양식명", example = "조직 몰입도")
    String formName,

    @Schema(description = "피평가자 사번", example = "EMP1023")
    String targetEmpNo,

    @Schema(description = "피평가자 이름", example = "김지원")
    String targetName,

    @Schema(description = "피평가자 부서", example = "개발팀")
    String departmentName,

    @Schema(description = "피평가자 직위", example = "과장")
    String positionName,

    @Schema(description = "평가자 사번", example = "EMP2098")
    String evaluatorEmpNo,

    @Schema(description = "평가자 이름", example = "박성민")
    String evaluatorName,

    @Schema(description = "평가 총점", example = "85")
    Integer score,

    @Schema(description = "제출일 (yyyy-MM-dd HH:mm)", example = "2025-06-25 14:30")
    String submittedAt

) {}
