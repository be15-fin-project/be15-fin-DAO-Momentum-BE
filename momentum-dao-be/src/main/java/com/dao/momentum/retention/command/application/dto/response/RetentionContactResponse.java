package com.dao.momentum.retention.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "근속 면담 응답 DTO")
public record RetentionContactResponse(

    @Schema(description = "면담 기록 ID", example = "1001")
    Long retentionId,

    @Schema(description = "면담 대상 사원 ID", example = "101")
    Long targetId,

    @Schema(description = "면담 진행자 ID", example = "201")
    Long managerId,

    @Schema(description = "면담 요청자 ID", example = "201")
    Long writerId,

    @Schema(description = "면담 사유", example = "근속 위험 신호로 인한 면담 실시")
    String reason,

    @Schema(description = "면담 등록일시", example = "2025-06-25T14:00:00")
    LocalDateTime createdAt
) {}
