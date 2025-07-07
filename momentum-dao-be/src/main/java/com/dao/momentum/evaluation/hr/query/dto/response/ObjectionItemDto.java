package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "인사 평가 이의제기 상세 조회 응답 DTO")
public record ObjectionItemDto(
        @Schema(description = "이의제기 ID", example = "5001")
        Long objectionId,

        @Schema(description = "평가 결과 ID", example = "10023")
        Long resultId,

        @Schema(description = "평가 대상자 사번", example = "20250001")
        String empNo,

        @Schema(description = "평가 대상자 이름", example = "김현우")
        String empName,

        @Schema(description = "평가 일시 (yyyy-MM-dd HH:mm:ss)", example = "2025-06-15 14:23:45")
        String evaluatedAt,

        @Schema(description = "이의제기 사유", example = "평가 점수가 과도하게 낮습니다.")
        String objectionReason,

        @Schema(description = "처리 상태", example = "PENDING")
        String statusType,

        @Schema(description = "처리 사유 (처리된 경우만)", example = "재평가 후 점수 조정")
        String responseReason
) {
}
