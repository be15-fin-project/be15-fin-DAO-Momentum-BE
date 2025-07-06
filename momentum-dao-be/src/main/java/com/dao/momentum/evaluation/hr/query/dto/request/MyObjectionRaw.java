package com.dao.momentum.evaluation.hr.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "Mapper가 매핑할 원시 점수 DTO")
public record MyObjectionRaw(
        @Schema(description = "이의제기 ID", example = "5001")
        Long objectionId,

        @Schema(description = "평가 결과 ID", example = "10023")
        Long resultId,

        @Schema(description = "평가 회차 번호", example = "10023")
        Integer roundNo,

        @Schema(description = "이의제기 상태 ID", example = "1")
        Integer statusId,

        @Schema(description = "이의제기 상태명", example = "PENDING")
        String statusType,

        @Schema(description = "평가 일시 (yyyy-MM-dd HH:mm:ss)", example = "2025-06-15 14:23:45")
        String createdAt,

        @Schema(description = "종합 점수 (원시)", example = "82")
        int overallScore
) {
}
