package com.dao.momentum.retention.prospect.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(description = "근속 전망 회차 Raw DTO (매퍼 결과용)")
public record RetentionRoundRawDto(
        @Schema(description = "회차 ID", example = "1")
        Integer roundId,

        @Schema(description = "회차 번호", example = "3")
        Integer roundNo,

        @Schema(description = "분석 연도", example = "2025")
        Integer year,

        @Schema(description = "분석 월", example = "6")
        Integer month,

        @Schema(description = "분석 시작일", example = "2025-06-01")
        LocalDate startDate,

        @Schema(description = "분석 종료일", example = "2025-06-30")
        LocalDate endDate,

        @Schema(description = "참여자 수", example = "42")
        Integer participantCount
) {
}
