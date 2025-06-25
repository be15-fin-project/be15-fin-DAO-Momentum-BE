package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "자가 진단 평가 목록 응답 DTO")
public class SelfEvaluationResponseDto {

    @Schema(description = "평가 결과 ID", example = "501")
    private Long resultId;

    @Schema(description = "평가자 사번", example = "20250001")
    private String empNo;

    @Schema(description = "평가자 이름", example = "김현우")
    private String evalName;

    @Schema(description = "평가 양식명", example = "직무 스트레스")
    private String formName;

    @Schema(description = "평가 회차 번호", example = "2")
    private Integer roundNo;

    @Schema(description = "종합 점수", example = "82")
    private Integer score;

    @Schema(description = "평가 사유", example = "책임감 있게 업무를 잘 수행함")
    private String reason;

    @Schema(description = "제출 일시", example = "2025-06-20T15:00:00")
    private LocalDateTime createdAt;
}
