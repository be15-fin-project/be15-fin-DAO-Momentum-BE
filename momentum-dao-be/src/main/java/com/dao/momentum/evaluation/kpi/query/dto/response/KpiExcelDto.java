package com.dao.momentum.evaluation.kpi.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "KPI 엑셀 다운로드 DTO")
public record KpiExcelDto(

    @Schema(description = "사번", example = "EMP2024001")
    String employeeNo,

    @Schema(description = "이름", example = "홍길동")
    String employeeName,

    @Schema(description = "부서명", example = "인사팀")
    String departmentName,

    @Schema(description = "직위명", example = "대리")
    String positionName,

    @Schema(description = "KPI 목표", example = "전사 ERP 고도화")
    String goal,

    @Schema(description = "KPI 목표 수치", example = "100")
    Integer goalValue,

    @Schema(description = "진행률 (%)", example = "75")
    Integer kpiProgress,

    @Schema(description = "KPI 상태", example = "ACCEPTED")
    String statusName,

    @Schema(description = "생성일 (yyyy-MM-dd)", example = "2025-05-01")
    String createdAt,

    @Schema(description = "마감일 (yyyy-MM-dd)", example = "2025-06-30")
    String deadline

) {}
