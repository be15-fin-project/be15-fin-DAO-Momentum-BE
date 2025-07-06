package com.dao.momentum.evaluation.hr.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "이의제기 처리 응답 DTO")
public record HrObjectionProcessResponseDto(
        @Schema(description = "처리된 이의 제기 ID", example = "12")
        Long objectionId,

        @Schema(description = "처리 결과", example = "승인" /* 또는 "반려" */)
        String result,

        @Schema(description = "처리 일시", example = "2025-06-25T16:40:00")
        String processedAt
) {
}
