package com.dao.momentum.retention.prospect.query.dto.response;

import com.dao.momentum.retention.prospect.command.domain.aggregate.StabilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "근속 전망 상세 조회 DTO")
public record RetentionSupportDetailDto(
        @Schema(description = "회차 번호", example = "5")
        Integer roundNo,

        @Schema(description = "사원 ID", example = "13")
        Long empId,

        @Schema(description = "사번", example = "20250020")
        String empNo,

        @Schema(description = "사원 이름", example = "홍길동")
        String empName,

        @Schema(description = "부서명", example = "프론트엔드팀")
        String deptName,

        @Schema(description = "직급명", example = "대리")
        String positionName,

        // 상급자 정보
        @Schema(description = "상급자 사원 ID", example = "102")
        Long managerId,

        @Schema(description = "상급자 사번", example = "20230005")
        String managerEmpNo,

        @Schema(description = "상급자 이름", example = "이상훈")
        String managerName,

        // 요인별 점수
        @Schema(description = "직무 만족도 점수", example = "82.5")
        Double jobScore,

        @Schema(description = "직무 만족도 등급", example = "우수")
        String jobGrade,

        @Schema(description = "보상 만족도 점수", example = "91.0")
        Double compScore,

        @Schema(description = "보상 만족도 등급", example = "탁월")
        String compGrade,

        @Schema(description = "관계 만족도 점수", example = "76.3")
        Double relationScore,

        @Schema(description = "관계 만족도 등급", example = "양호")
        String relationGrade,

        @Schema(description = "성장 만족도 점수", example = "59.8")
        Double growthScore,

        @Schema(description = "성장 만족도 등급", example = "주의")
        String growthGrade,

        @Schema(description = "워라밸 만족도 점수", example = "45.2")
        Double wlbScore,

        @Schema(description = "워라밸 만족도 등급", example = "미흡")
        String wlbGrade,

        @Schema(description = "근속 연수 (년)", example = "3.25")
        Double tenure,

        @Schema(description = "근속 연수 점수", example = "62.5")
        Double tenureScore,

        @Schema(description = "근속 연수 등급", example = "양호")
        String tenureGrade,

        @Schema(description = "종합 근속 전망 점수", example = "78.1")
        Double retentionScore,

        @Schema(description = "종합 근속 전망 등급", example = "우수")
        String retentionGrade,

        @Schema(description = "안정성 유형", example = "STABLE")
        StabilityType stabilityType
) {
}
