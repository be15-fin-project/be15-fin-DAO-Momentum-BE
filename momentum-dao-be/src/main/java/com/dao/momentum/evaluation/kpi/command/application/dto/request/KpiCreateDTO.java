package com.dao.momentum.evaluation.kpi.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@Schema(description = "KPI 생성 내부 DTO")
public class KpiCreateDTO {

    @Schema(description = "KPI 목표", example = "분기 매출 10억 달성")
    private String goal;

    @Schema(description = "KPI 목표 수치", example = "1000000000")
    private Integer goalValue;

    @Schema(description = "KPI 진척도", example = "0")
    private Integer kpiProgress;

    @Schema(description = "25% 달성 기준", example = "계약 건수 5건")
    private String progress25;

    @Schema(description = "50% 달성 기준", example = "계약 건수 10건")
    private String progress50;

    @Schema(description = "75% 달성 기준", example = "계약 건수 15건")
    private String progress75;

    @Schema(description = "100% 달성 기준", example = "계약 건수 20건")
    private String progress100;

    @Schema(description = "마감 기한", example = "2025-12-31")
    private LocalDate deadline;

    @Schema(description = "KPI 상태 ID (기본값: 1 = 작성 대기)", example = "1")
    private final Integer statusId = 1;
}
