package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "인사 평가 이의제기 상세 조회 응답 DTO")
public class ObjectionItemDto {

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

    @Schema(description = "이의제기 사유", example = "평가 점수가 과도하게 낮습니다.")
    private String objectionReason;

    @Schema(description = "처리 상태", example = "PENDING")
    private String statusType;

    @Schema(description = "처리 사유 (처리된 경우만)", example = "재평가 후 점수 조정")
    private String responseReason;
}
