package com.dao.momentum.evaluation.eval.query.dto.response.various;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "자가 진단 평가 목록 응답 DTO")
@Builder
public record SelfEvaluationResponseDto(

        @Schema(description = "평가 결과 ID", example = "501")
        Long resultId,

        @Schema(description = "평가자 사번", example = "20250001")
        String empNo,

        @Schema(description = "평가자 이름", example = "김현우")
        String evalName,

        @Schema(description = "평가 양식명", example = "직무 스트레스")
        String formName,

        @Schema(description = "평가 회차 번호", example = "2")
        Integer roundNo,

        @Schema(description = "종합 점수", example = "82")
        Integer score,

        @Schema(description = "평가 사유", example = "책임감 있게 업무를 잘 수행함")
        String reason,

        @Schema(description = "제출 일시", example = "2025-06-20T15:00:00")
        LocalDateTime createdAt
) { }
