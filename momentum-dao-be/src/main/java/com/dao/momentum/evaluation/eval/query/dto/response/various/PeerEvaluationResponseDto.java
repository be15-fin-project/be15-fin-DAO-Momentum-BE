package com.dao.momentum.evaluation.eval.query.dto.response.various;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "사원 간 평가 응답 DTO")
@Builder
public record PeerEvaluationResponseDto(

        @Schema(description = "평가 결과 ID", example = "501")
        Long resultId,

        @Schema(description = "평가자 사번", example = "20250001")
        String evalNo,

        @Schema(description = "평가자 이름", example = "김현우")
        String evalName,

        @Schema(description = "피평가자 사번", example = "20250002")
        String targetNo,

        @Schema(description = "피평가자 이름", example = "정예준")
        String targetName,

        @Schema(description = "평가 양식명", example = "동료 평가")
        String formName,

        @Schema(description = "평가 회차 번호", example = "2")
        Integer roundNo,

        @Schema(description = "총점", example = "85")
        Integer score,

        @Schema(description = "평가 사유", example = "책임감 있게 업무를 잘 수행함")
        String reason,

        @Schema(description = "평가 제출일시", example = "2025-06-18T15:30:00")
        LocalDateTime createdAt
) { }
