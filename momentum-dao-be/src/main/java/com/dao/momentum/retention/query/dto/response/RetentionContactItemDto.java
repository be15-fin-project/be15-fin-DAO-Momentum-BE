package com.dao.momentum.retention.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "면담 기록 항목 DTO")
public class RetentionContactItemDto {

    @Schema(description = "면담 요청 일시", example = "2025-06-01T09:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "사번", example = "20240001")
    private String empNo;

    @Schema(description = "대상자 이름", example = "김예은")
    private String targetName;

    @Schema(description = "부서 이름", example = "인사팀")
    private String deptName;

    @Schema(description = "직급 이름", example = "과장")
    private String positionName;

    @Schema(description = "상급자 이름", example = "김하윤")
    private String managerName;

    @Schema(description = "면담 요청 사유")
    private String reason;
}
