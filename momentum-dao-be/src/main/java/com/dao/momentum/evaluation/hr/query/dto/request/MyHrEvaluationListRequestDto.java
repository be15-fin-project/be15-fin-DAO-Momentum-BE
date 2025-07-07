package com.dao.momentum.evaluation.hr.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Schema(description = "사원 본인 인사평가 결과 목록 조회 요청 DTO (회차 날짜 기준 기간 조회 지원)")
public record MyHrEvaluationListRequestDto(
        @Schema(description = "평가 회차 ID", example = "10023")
        Integer roundId,

        @Schema(description = "조회 시작일 (yyyy-MM-dd)", example = "2025-01-01")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @Schema(description = "조회 종료일 (yyyy-MM-dd)", example = "2025-06-30")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        @Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
        Integer page,

        @Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
        Integer size
) {

    // 기본값 처리
    public MyHrEvaluationListRequestDto {
        if (page == null) {
            page = 1;
        }
        if (size == null) {
            size = 10;
        }
    }

    public int getOffset() {
        return (page - 1) * size;
    }
}
