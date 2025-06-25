package com.dao.momentum.retention.interview.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "근속 면담 생성 DTO")
public record RetentionContactCreateDto(

        @Schema(description = "면담 대상 사원 ID", example = "101")
        Long targetId,

        @Schema(description = "면담 진행자 ID", example = "201")
        Long managerId,

        @Schema(description = "면담 요청자 ID", example = "201")
        Long writerId,

        @Schema(description = "면담 사유", example = "근속 위험 신호로 인한 면담 실시")
        String reason
) {}
