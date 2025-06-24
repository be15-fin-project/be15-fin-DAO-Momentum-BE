package com.dao.momentum.evaluation.kpi.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "KPI 취소 요청")
public class KpiCancelRequest {

    @NotBlank
    @Schema(description = "KPI 취소 사유", example = "목표 변경으로 인한 취소 요청")
    private String reason;
}
