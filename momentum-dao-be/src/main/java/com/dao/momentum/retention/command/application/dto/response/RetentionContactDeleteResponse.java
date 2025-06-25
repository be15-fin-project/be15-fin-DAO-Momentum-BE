package com.dao.momentum.retention.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "근속 면담 삭제 응답 DTO")
public record RetentionContactDeleteResponse(

        @Schema(description = "삭제된 면담 ID", example = "1001")
        Long retentionId,

        @Schema(description = "응답 메시지", example = "면담이 성공적으로 삭제되었습니다.")
        String message
) {}
