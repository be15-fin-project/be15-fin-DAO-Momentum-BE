package com.dao.momentum.evaluation.kpi.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "KPI 취소 요청")
public record KpiCancelRequest(

        @NotBlank
        @Schema(description = "KPI 취소 사유", example = "목표 변경으로 인한 취소 요청")
        String reason
) {}
