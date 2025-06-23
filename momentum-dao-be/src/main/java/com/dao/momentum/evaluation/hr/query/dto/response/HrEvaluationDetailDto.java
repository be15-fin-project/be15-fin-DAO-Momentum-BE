package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "인사 평가 상세 조회 응답 DTO")
public class HrEvaluationDetailDto {

    @Schema(description = "평가 결과 ID", example = "10023")
    private Long resultId;

    @Schema(description = "사원 번호", example = "20250001")
    private String empNo;

    @Schema(description = "사원 이름", example = "김현우")
    private String empName;

    @Schema(description = "평가 일시", example = "2025-06-15T14:23:45")
    private LocalDateTime evaluatedAt;

    @Schema(description = "종합 등급", example = "우수")
    private String overallGrade;

}
