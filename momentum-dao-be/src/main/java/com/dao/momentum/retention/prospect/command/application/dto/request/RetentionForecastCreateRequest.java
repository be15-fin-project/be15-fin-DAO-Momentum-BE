package com.dao.momentum.retention.prospect.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Schema(description = "근속 전망 생성 요청 DTO")
public record RetentionForecastCreateRequest(

        @NotNull(message = "년도는 필수입니다.")
        @Min(value = 2000, message = "년도는 2000년 이상이어야 합니다.")
        @Max(value = 2100, message = "년도는 2100년 이하여야 합니다.")
        @Schema(description = "분석 대상 연도", example = "2025", minimum = "2000", maximum = "2100")
        Integer year,

        @NotNull(message = "월은 필수입니다.")
        @Min(value = 1, message = "월은 1 이상이어야 합니다.")
        @Max(value = 12, message = "월은 12 이하여야 합니다.")
        @Schema(description = "분석 대상 월", example = "6", minimum = "1", maximum = "12")
        Integer month
) {}
