package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "인사 평가 상세 조회 응답 DTO")
public record HrEvaluationDetailDto(
        @Schema(description = "평가 결과 ID", example = "10023")
        Long resultId,

        @Schema(description = "사원 번호", example = "20250001")
        String empNo,

        @Schema(description = "사원 이름", example = "김현우")
        String empName,

        @Schema(description = "평가 일시", example = "2025-06-15T14:23:45")
        LocalDateTime evaluatedAt,

        @Schema(description = "종합 등급", example = "우수")
        String overallGrade
) {
}
