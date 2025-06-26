package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "자가 평가 엑셀 다운로드 DTO")
public record SelfEvaluationExcelDto(

    @Schema(description = "회차 번호", example = "2025-01")
    String roundNo,

    @Schema(description = "사번", example = "EMP1204")
    String empNo,

    @Schema(description = "이름", example = "최유진")
    String name,

    @Schema(description = "부서명", example = "재무팀")
    String departmentName,

    @Schema(description = "직위명", example = "대리")
    String positionName,

    @Schema(description = "평가 점수", example = "88")
    Integer score,

    @Schema(description = "제출일시 (yyyy-MM-dd HH:mm)", example = "2025-06-24 10:45")
    String submittedAt

) {}
