package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "사원 인사평가 단일 내역 DTO")
public class HrEvaluationItemDto {

    @Schema(description = "평가 회차 번호", example = "5")
    private int roundNo;

    @Schema(description = "평가 결과 ID", example = "10023")
    private Long resultId;

    @Schema(description = "종합 등급 (예: A, B, C 등)")
    private String overallGrade;

    @Schema(description = "평가 일시 (yyyy-MM-dd HH:mm:ss)", example = "2025-06-15 14:23:45")
    private LocalDateTime evaluatedAt;
}
