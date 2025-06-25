package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "사원 간 평가 응답 DTO")
public class PeerEvaluationResponseDto {

    @Schema(description = "평가 결과 ID", example = "501")
    private Long resultId;

    @Schema(description = "평가자 사번", example = "20250001")
    private String evalNo;

    @Schema(description = "평가자 이름", example = "김현우")
    private String evalName;

    @Schema(description = "피평가자 사번", example = "20250002")
    private String targetNo;

    @Schema(description = "피평가자 이름", example = "정예준")
    private String targetName;

    @Schema(description = "평가 양식명", example = "동료 평가")
    private String formName;

    @Schema(description = "평가 회차 번호", example = "2")
    private Integer roundNo;

    @Schema(description = "총점", example = "85")
    private Integer score;

    @Schema(description = "평가 사유", example = "책임감 있게 업무를 잘 수행함")
    private String reason;

    @Schema(description = "평가 제출일시", example = "2025-06-18T15:30:00")
    private LocalDateTime createdAt;
}
