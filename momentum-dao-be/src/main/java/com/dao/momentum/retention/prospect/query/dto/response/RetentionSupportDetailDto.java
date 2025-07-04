package com.dao.momentum.retention.prospect.query.dto.response;

import com.dao.momentum.retention.prospect.command.domain.aggregate.StabilityType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "근속 전망 상세 조회 DTO")
public class RetentionSupportDetailDto {

    @Schema(description = "회차 번호", example = "5")
    private Integer roundNo;

    @Schema(description = "사원 ID", example = "13")
    private Long empId;

    @Schema(description = "사번", example = "20250020")
    private String empNo;

    @Schema(description = "사원 이름", example = "홍길동")
    private String empName;

    @Schema(description = "상급자 사원 ID", example = "102")
    private Long managerId;

    @Schema(description = "상급자 사번", example = "20230005")
    private String managerEmpNo;

    @Schema(description = "상급자 이름", example = "이상훈")
    private String managerName;

    @Schema(description = "부서명", example = "프론트엔드팀")
    private String deptName;

    @Schema(description = "직급명", example = "대리")
    private String positionName;

    @Schema(description = "직무 만족도 점수", example = "82.5")
    private Double jobScore;

    @Schema(description = "직무 만족도 등급", example = "우수")
    private String jobGrade;

    @Schema(description = "보상 만족도 점수", example = "91.0")
    private Double compScore;

    @Schema(description = "보상 만족도 등급", example = "탁월")
    private String compGrade;

    @Schema(description = "관계 만족도 점수", example = "76.3")
    private Double relationScore;

    @Schema(description = "관계 만족도 등급", example = "양호")
    private String relationGrade;

    @Schema(description = "성장 만족도 점수", example = "59.8")
    private Double growthScore;

    @Schema(description = "성장 만족도 등급", example = "주의")
    private String growthGrade;

    @Schema(description = "워라밸 만족도 점수", example = "45.2")
    private Double wlbScore;

    @Schema(description = "워라밸 만족도 등급", example = "미흡")
    private String wlbGrade;

    @Schema(description = "근속 연수 (년)", example = "3.25")
    private Double tenure;

    @Schema(description = "근속 연수 점수", example = "62.5")
    private Double tenureScore;

    @Schema(description = "근속 연수 등급", example = "양호")
    private String tenureGrade;

    @Schema(description = "종합 근속 전망 점수", example = "78.1")
    private Double retentionScore;

    @Schema(description = "종합 근속 전망 등급", example = "우수")
    private String retentionGrade;

    @Schema(description = "안정성 유형", example = "STABLE")
    private StabilityType stabilityType;
}
