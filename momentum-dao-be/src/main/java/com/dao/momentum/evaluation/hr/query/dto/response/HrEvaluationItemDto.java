package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "사원 인사평가 단일 내역 DTO")
public record HrEvaluationItemDto(
        @Schema(description = "평가 회차 번호", example = "5")
        int roundNo,

        @Schema(description = "평가 결과 ID", example = "10023")
        Long resultId,

        @Schema(description = "종합 등급 (예: A, B, C 등)")
        String overallGrade,

        @Schema(description = "평가 일시 (yyyy-MM-dd HH:mm:ss)", example = "2025-06-15 14:23:45")
        LocalDateTime evaluatedAt,

        @Schema(description = "이의제기 제출 여부", example = "true")
        boolean objectionSubmitted
) {
}
