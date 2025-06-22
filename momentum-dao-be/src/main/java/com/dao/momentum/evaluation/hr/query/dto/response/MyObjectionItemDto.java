package com.dao.momentum.evaluation.hr.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@Schema(description = "사원이 조회하는 이의제기 목록 항목 DTO")
public class MyObjectionItemDto {

    @Schema(description = "이의제기 ID", example = "5001")
    private Long objectionId;

    @Schema(description = "평가 결과 ID", example = "10023")
    private Long resultId;

    @Schema(description = "이의제기 상태명", example = "PENDING")
    private String statusType;

    @Schema(description = "평가 일시 (yyyy-MM-dd HH:mm:ss)", example = "2025-06-15 14:23:45")
    private String createdAt;

    @Schema(description = "종합 등급", example = "우수함")
    private String overallGrade;
}
