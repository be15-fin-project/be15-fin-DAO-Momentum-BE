package com.dao.momentum.retention.prospect.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Schema(description = "근속 전망 회차 목록 조회 요청 DTO")
public record RetentionRoundSearchRequestDto(
        @Schema(description = "분석 상태 필터 (예정, 진행 중, 완료)", example = "진행 중")
        String status,

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

    // 레코드에서 기본값을 설정할 수 없으므로, 생성자에서 기본값을 설정
    public RetentionRoundSearchRequestDto {
        if (page == null) page = 1;
        if (size == null) size = 10;
    }

    public int getOffset() {
        return (page - 1) * size;
    }
}
