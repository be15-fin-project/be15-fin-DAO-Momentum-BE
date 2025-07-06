package com.dao.momentum.evaluation.kpi.query.dto.response;

import com.dao.momentum.common.dto.UseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "KPI 상세 조회 응답 DTO")
public record KpiDetailResponseDto(

        @Schema(description = "KPI 식별자", example = "101")
        Long kpiId,

        // 작성자 정보
        @Schema(description = "사원 번호", example = "20240001")
        String empNo,

        @Schema(description = "사원 이름", example = "김현우")
        String employeeName,

        @Schema(description = "부서명", example = "기획팀")
        String departmentName,

        @Schema(description = "직위명", example = "대리")
        String positionName,

        // KPI 목표 정보
        @Schema(description = "KPI 목표 내용", example = "월간 영업 건수 10건 달성")
        String goal,

        @Schema(description = "KPI 목표 수치", example = "10")
        int goalValue,

        @Schema(description = "KPI 현재 진척도 (%)", example = "40")
        int kpiProgress,

        // 진척 기준 정보
        @Schema(description = "진척도 25% 기준 설명", example = "영업 목표 2건 달성")
        String progress25,

        @Schema(description = "진척도 50% 기준 설명", example = "영업 목표 5건 달성")
        String progress50,

        @Schema(description = "진척도 75% 기준 설명", example = "영업 목표 8건 달성")
        String progress75,

        @Schema(description = "진척도 100% 기준 설명", example = "영업 목표 10건 달성")
        String progress100,

        // 상태 및 기간 정보
        @Schema(description = "KPI 상태 타입",
                example = "PENDING",
                allowableValues = {"PENDING", "ACCEPTED", "REJECTED", "CANCELLED", "DELETED"})
        String statusType,

        @Schema(description = "처리 사유", example = "합당한 KPI 확인했습니다.")
        String reason,

        @Schema(description = "취소 사유", example = "해당 KPI의 진행은 무리입니다")
        String cancelReason,

        @Schema(description = "취소 처리 사유", example = "해당 KPI가 무리인 것을 확인했습니다")
        String cancelResponse,

        @Schema(description = "삭제 여부 (Y, N)", example = "Y")
        UseStatus isDeleted,

        @Schema(description = "KPI 작성일", example = "2025-06-01")
        String createdAt,

        @Schema(description = "KPI 마감 기한", example = "2025-06-30")
        String deadline

) {}
