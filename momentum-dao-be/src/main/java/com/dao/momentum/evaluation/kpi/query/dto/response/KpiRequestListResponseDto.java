package com.dao.momentum.evaluation.kpi.query.dto.response;

import com.dao.momentum.common.dto.UseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "KPI 요청 목록 응답 DTO")
public record KpiRequestListResponseDto(

        @Schema(description = "KPI ID", example = "101")
        Long kpiId,

        @Schema(description = "사원 번호", example = "20240001")
        String empNo,

        @Schema(description = "사원 이름", example = "김현우")
        String employeeName,

        @Schema(description = "부서명", example = "기획팀")
        String departmentName,

        @Schema(description = "직위명", example = "대리")
        String positionName,

        @Schema(description = "KPI 목표 내용", example = "월간 영업 건수 10건 달성")
        String goal,

        @Schema(description = "KPI 목표 수치", example = "10")
        Integer goalValue,

        @Schema(description = "KPI 현재 진척도 (%)", example = "60")
        Integer kpiProgress,

        @Schema(description = "KPI 상태명", example = "승인 대기")
        String statusName,

        @Schema(description = "작성일", example = "2025-06-01")
        String createdAt,

        @Schema(description = "마감일", example = "2025-06-30")
        String deadline,

        @Schema(description = "삭제 여부 (Y, N)", example = "Y")
        UseStatus isDeleted

) {}
