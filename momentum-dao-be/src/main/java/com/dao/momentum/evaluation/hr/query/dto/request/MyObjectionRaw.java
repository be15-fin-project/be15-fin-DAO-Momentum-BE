package com.dao.momentum.evaluation.hr.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor
@Schema(description = "Mapper가 매핑할 원시 점수 DTO")
public class MyObjectionRaw {

    @Schema(description = "이의제기 ID", example = "5001")
    private Long objectionId;

    @Schema(description = "평가 결과 ID", example = "10023")
    private Long resultId;

    @Schema(description = "평가 회차 번호", example = "10023")
    private Integer roundNo;

    @Schema(description = "이의제기 상태 ID", example = "1")
    private Integer statusId;

    @Schema(description = "이의제기 상태명", example = "PENDING")
    private String statusType;

    @Schema(description = "평가 일시 (yyyy-MM-dd HH:mm:ss)", example = "2025-06-15 14:23:45")
    private String createdAt;

    @Schema(description = "종합 점수 (원시)", example = "82")
    private int overallScore;
}
