package com.dao.momentum.retention.interview.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@Schema(description = "면담 기록 항목 DTO")
public record RetentionContactItemDto(
        @Schema(description = "면담 ID", example = "12")
        Integer retentionId,

        @Schema(description = "면담 요청 일시", example = "2025-06-01T09:00:00")
        LocalDateTime createdAt,

        @Schema(description = "사번", example = "20240001")
        String empNo,

        @Schema(description = "대상자 이름", example = "김예은")
        String targetName,

        @Schema(description = "부서 이름", example = "인사팀")
        String deptName,

        @Schema(description = "직급 이름", example = "과장")
        String positionName,

        @Schema(description = "상급자 이름", example = "김하윤")
        String managerName,

        @Schema(description = "면담 요청 사유")
        String reason
) {
}
