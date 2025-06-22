package com.dao.momentum.evaluation.eval.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "조직 평가 내역 응답 DTO")
public class OrgEvaluationResponseDto {

    @Schema(description = "평가 결과 ID", example = "501")
    private Long resultId;

    @Schema(description = "평가 양식명", example = "조직 몰입도")
    private String formName;

    @Schema(description = "회차 번호", example = "2")
    private Integer roundNo;

    @Schema(description = "종합 점수", example = "78")
    private Integer score;

    @Schema(description = "제출 일시", example = "2025-06-18T15:30:00")
    private LocalDateTime createdAt;
}
