package com.dao.momentum.evaluation.kpi.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "KPI 사원별 요약 응답 DTO")
public record KpiEmployeeSummaryResponseDto(

        @Schema(description = "사원 번호", example = "20240001")
        String empNo,

        @Schema(description = "사원 이름", example = "김현우")
        String employeeName,

        @Schema(description = "부서명", example = "기획팀")
        String departmentName,

        @Schema(description = "직위명", example = "대리")
        String positionName,

        @Schema(description = "전체 KPI 수", example = "20")
        int totalKpiCount,

        @Schema(description = "완료된 KPI 수", example = "12")
        int completedKpiCount,

        @Schema(description = "평균 진척도 (%)", example = "67.5")
        double averageProgress,

        @Schema(description = "KPI 완료율 (%)", example = "60.0")
        double completionRate

) {}
