package com.dao.momentum.retention.prospect.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "개별 근속 지원 데이터 DTO")
public record RetentionSupportDto(

        @Schema(description = "직무 만족도 (0~100)", example = "85")
        int jobLevel,

        @Schema(description = "보상 만족도 (0~100)", example = "75")
        int compLevel,

        @Schema(description = "관계 만족도 (0~100)", example = "80")
        int relationLevel,

        @Schema(description = "성장 만족도 (0~100)", example = "70")
        int growthLevel,

        @Schema(description = "근속 연수", example = "3.5")
        BigDecimal tenureLevel,

        @Schema(description = "워라밸 만족도 (0~100)", example = "90")
        int wlbLevel,

        @Schema(description = "최종 근속 전망 점수 (0~100)", example = "78")
        int retentionScore
) {}
