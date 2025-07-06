package com.dao.momentum.retention.prospect.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "근속 전망 회차 목록 응답 DTO")
public record RetentionRoundListResponseDto(
        @Schema(description = "회차 ID", example = "1")
        Integer roundId,

        @Schema(description = "회차 번호", example = "5")
        Integer roundNo,

        @Schema(description = "분석 연도", example = "2025")
        Integer year,

        @Schema(description = "분석 월", example = "6")
        Integer month,

        @Schema(description = "분석 기간 시작일", example = "2025-06-01")
        String periodStart,

        @Schema(description = "분석 기간 종료일", example = "2025-06-30")
        String periodEnd,

        @Schema(description = "참여자 수", example = "45")
        Integer participantCount
) {
}
