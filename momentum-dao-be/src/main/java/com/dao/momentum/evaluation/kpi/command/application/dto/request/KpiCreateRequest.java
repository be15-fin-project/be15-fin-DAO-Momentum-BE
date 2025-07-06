package com.dao.momentum.evaluation.kpi.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDate;

@Builder
@Schema(description = "KPI 생성 요청")
public record KpiCreateRequest(

        @NotBlank
        @Schema(description = "KPI 목표", example = "분기 매출 10억 달성")
        String goal,

        @NotNull
        @Positive
        @Schema(description = "KPI 목표 수치", example = "1000000000")
        Integer goalValue,

        @NotNull
        @Min(0)
        @Max(100)
        @Schema(description = "KPI 진척도 (0~100)", example = "0")
        Integer kpiProgress,

        @NotBlank
        @Schema(description = "25% 달성 기준", example = "계약 건수 5건")
        String progress25,

        @NotBlank
        @Schema(description = "50% 달성 기준", example = "계약 건수 10건")
        String progress50,

        @NotBlank
        @Schema(description = "75% 달성 기준", example = "계약 건수 15건")
        String progress75,

        @NotBlank
        @Schema(description = "100% 달성 기준", example = "계약 건수 20건")
        String progress100,

        @NotNull
        @Future
        @Schema(description = "마감 기한", example = "2025-12-31")
        LocalDate deadline
) {

    public KpiCreateDTO toDTO() {
        return KpiCreateDTO.builder()
                .goal(this.goal)
                .goalValue(this.goalValue)
                .kpiProgress(this.kpiProgress)
                .progress25(this.progress25)
                .progress50(this.progress50)
                .progress75(this.progress75)
                .progress100(this.progress100)
                .deadline(this.deadline)
                .build();
    }

}
