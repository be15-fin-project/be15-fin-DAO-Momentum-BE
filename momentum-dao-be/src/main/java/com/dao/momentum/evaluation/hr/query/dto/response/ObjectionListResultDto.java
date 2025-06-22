package com.dao.momentum.evaluation.hr.query.dto.response;

import com.dao.momentum.evaluation.eval.query.dto.response.FactorScoreDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "인사 평가 이의제기 상세 조회 응답 DTO")
public class ObjectionListResultDto {

    @Schema(description = "이의제기 ID", example = "5001")
    private Long objectionId;

    @Schema(description = "평가 결과 ID", example = "10023")
    private Long resultId;

    @Schema(description = "평가 대상자 사번", example = "20250001")
    private String empNo;

    @Schema(description = "평가 대상자 이름", example = "김현우")
    private String empName;

    @Schema(description = "평가 일시 (yyyy-MM-dd HH:mm:ss)", example = "2025-06-15 14:23:45")
    private String evaluatedAt;

    // 가중치 (hr_weight)
    @Schema(description = "가중치: 업무 수행 역량 (%)", example = "20")
    private Integer weightPerform;

    @Schema(description = "가중치: 협업 역량 (%)", example = "15")
    private Integer weightTeam;

    @Schema(description = "가중치: 자기관리 및 태도 (%)", example = "15")
    private Integer weightAttitude;

    @Schema(description = "가중치: 성장 의지 (%)", example = "25")
    private Integer weightGrowth;

    @Schema(description = "가중치: 조직 기여도 (%)", example = "20")
    private Integer weightEngagement;

    @Schema(description = "가중치: KPI 성과 관리 (%)", example = "5")
    private Integer weightResult;

    // 등급 비율 (hr_rate)
    @Schema(description = "S 등급 비율 (%)", example = "5")
    private Integer rateS;

    @Schema(description = "A 등급 비율 (%)", example = "20")
    private Integer rateA;

    @Schema(description = "B 등급 비율 (%)", example = "35")
    private Integer rateB;

    @Schema(description = "C 등급 비율 (%)", example = "30")
    private Integer rateC;

    @Schema(description = "D 등급 비율 (%)", example = "10")
    private Integer rateD;

    @Schema(description = "이의제기 사유", example = "평가 점수가 과도하게 낮습니다.")
    private String objectionReason;

    @Schema(description = "처리 상태", example = "PENDING")
    private String statusType;

    @Schema(description = "처리 사유 (처리된 경우만)", example = "재평가 후 점수 조정")
    private String responseReason;
}
