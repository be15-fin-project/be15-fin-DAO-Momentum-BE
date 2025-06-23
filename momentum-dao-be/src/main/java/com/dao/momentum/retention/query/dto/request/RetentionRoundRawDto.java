package com.dao.momentum.retention.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "근속 전망 회차 Raw DTO (매퍼 결과용)")
public class RetentionRoundRawDto {

    @Schema(description = "회차 ID", example = "1")
    private Integer roundId;

    @Schema(description = "회차 번호", example = "3")
    private Integer roundNo;

    @Schema(description = "분석 연도", example = "2025")
    private Integer year;

    @Schema(description = "분석 월", example = "6")
    private Integer month;

    @Schema(description = "분석 시작일", example = "2025-06-01")
    private LocalDate startDate;

    @Schema(description = "분석 종료일", example = "2025-06-30")
    private LocalDate endDate;

    @Schema(description = "분석 상태", example = "진행 중")
    private String status;

    @Schema(description = "참여자 수", example = "42")
    private Integer participantCount;
}
