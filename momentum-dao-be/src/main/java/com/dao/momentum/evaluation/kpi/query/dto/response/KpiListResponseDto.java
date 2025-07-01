package com.dao.momentum.evaluation.kpi.query.dto.response;

import com.dao.momentum.common.dto.UseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "KPI 목록 응답 DTO")
public class KpiListResponseDto {

    @Schema(description = "KPI 식별자", example = "101")
    private Long kpiId;

    @Schema(description = "사원 번호", example = "20240001")
    private String empNo;

    @Schema(description = "사원 이름", example = "김현우")
    private String employeeName;

    @Schema(description = "부서명", example = "기획팀")
    private String departmentName;

    @Schema(description = "직위명", example = "대리")
    private String positionName;

    @Schema(description = "KPI 목표 내용", example = "월간 영업 건수 10건 달성")
    private String goal;

    @Schema(description = "KPI 목표 수치", example = "10")
    private int goalValue;

    @Schema(description = "현재 진척도(%)", example = "40")
    private int kpiProgress;

    @Schema(description = "KPI 상태명", example = "진행 중")
    private String statusName;

    @Schema(description = "KPI 등록일", example = "2025-06-01")
    private String createdAt;

    @Schema(description = "KPI 마감일", example = "2025-06-30")
    private String deadline;

    @Schema(description = "삭제 여부 (Y, N)", example = "Y")
    private UseStatus isDeleted;
}
