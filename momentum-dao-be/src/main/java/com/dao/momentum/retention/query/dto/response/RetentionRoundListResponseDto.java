package com.dao.momentum.retention.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "근속 전망 회차 목록 응답 DTO")
public class RetentionRoundListResponseDto {

    @Schema(description = "회차 ID", example = "1")
    private Integer roundId;

    @Schema(description = "회차 번호", example = "5")
    private Integer roundNo;

    @Schema(description = "분석 연도", example = "2025")
    private Integer year;

    @Schema(description = "분석 월", example = "6")
    private Integer month;

    @Schema(description = "분석 기간 시작일", example = "2025-06-01")
    private String periodStart;

    @Schema(description = "분석 기간 종료일", example = "2025-06-30")
    private String periodEnd;

    @Schema(description = "분석 상태 (예정/진행 중/완료)", example = "진행 중")
    private String status;

    @Schema(description = "참여자 수", example = "45")
    private Integer participantCount;
}
