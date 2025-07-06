package com.dao.momentum.evaluation.hr.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "인사 평가 이의제기 생성 응답 DTO")
public record HrObjectionCreateResponse(
        @Schema(description = "이의제기 ID", example = "501")
        Long objectionId,

        @Schema(description = "작성자 ID", example = "501")
        Long writerId,

        @Schema(description = "이의제기 상태", example = "PENDING")
        String status,

        @Schema(description = "이의제기 생성일시", example = "2025-06-25T15:30:00")
        LocalDateTime createdAt
) {
}
