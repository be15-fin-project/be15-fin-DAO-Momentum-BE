package com.dao.momentum.evaluation.hr.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "인사 평가 이의제기 생성 응답 DTO")
public class HrObjectionCreateResponse {

    @Schema(description = "이의제기 ID", example = "501")
    private Long objectionId;

    @Schema(description = "이의제기 상태", example = "PENDING")
    private String status;

    @Schema(description = "이의제기 생성일시", example = "2025-06-25T15:30:00")
    private LocalDateTime createdAt;
}