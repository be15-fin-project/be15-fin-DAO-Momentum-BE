package com.dao.momentum.evaluation.kpi.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "KPI 상세 조회 응답 DTO")
public class KpiDetailResponseDto {

    @Schema(description = "KPI 식별자", example = "101")
    private Long kpiId;

    // 작성자 정보
    @Schema(description = "사원 번호", example = "20240001")
    private String empNo;

    @Schema(description = "사원 이름", example = "김현우")
    private String employeeName;

    @Schema(description = "부서명", example = "기획팀")
    private String departmentName;

    @Schema(description = "직위명", example = "대리")
    private String positionName;

    // KPI 목표 정보
    @Schema(description = "KPI 목표 내용", example = "월간 영업 건수 10건 달성")
    private String goal;

    @Schema(description = "KPI 목표 수치", example = "10")
    private int goalValue;

    @Schema(description = "KPI 현재 진척도 (%)", example = "40")
    private int kpiProgress;

    // 진척 기준 정보
    @Schema(description = "진척도 25% 기준 설명", example = "영업 목표 2건 달성")
    private String progress25;

    @Schema(description = "진척도 50% 기준 설명", example = "영업 목표 5건 달성")
    private String progress50;

    @Schema(description = "진척도 75% 기준 설명", example = "영업 목표 8건 달성")
    private String progress75;

    @Schema(description = "진척도 100% 기준 설명", example = "영업 목표 10건 달성")
    private String progress100;

    // 상태 및 기간 정보
    @Schema(description = "KPI 상태 타입",
            example = "PENDING",
            allowableValues = {"PENDING", "ACCEPTED", "REJECTED", "CANCELLED", "DELETED"})
    private String statusType;

    @Schema(description = "KPI 작성일", example = "2025-06-01")
    private String createdAt;

    @Schema(description = "KPI 마감 기한", example = "2025-06-30")
    private String deadline;
}
