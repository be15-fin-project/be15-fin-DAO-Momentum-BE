package com.dao.momentum.evaluation.eval.query.dto.response.various;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Schema(description = "조직 평가 내역 응답 DTO")
@Builder
public record OrgEvaluationResponseDto(

        @Schema(description = "평가 결과 ID", example = "501")
        Long resultId,

        @Schema(description = "평가자 사번", example = "20250001")
        String empNo,

        @Schema(description = "평가자 이름", example = "김현우")
        String evalName,

        @Schema(description = "평가 양식명", example = "조직 몰입도")
        String formName,

        @Schema(description = "회차 번호", example = "2")
        Integer roundNo,

        @Schema(description = "종합 점수", example = "78")
        Integer score,

        @Schema(description = "평가 사유", example = "책임감 있게 업무를 잘 수행함")
        String reason,

        @Schema(description = "제출 일시", example = "2025-06-18T15:30:00")
        LocalDateTime createdAt
) { }
