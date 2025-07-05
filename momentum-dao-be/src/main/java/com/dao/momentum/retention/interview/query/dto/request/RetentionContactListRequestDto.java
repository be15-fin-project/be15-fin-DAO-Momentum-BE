package com.dao.momentum.retention.interview.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Builder
@Schema(description = "면담 기록 목록 조회 요청 DTO")
public record RetentionContactListRequestDto(
        @Schema(description = "사번", example = "20240001")
        String targetNo,

        @Schema(description = "상급자 ID", example = "34")
        Long managerId,

        @Schema(description = "상급자 사번", example = "34")
        String managerNo,

        @Schema(description = "부서 ID (필터)", example = "5")
        Integer deptId,

        @Schema(description = "직위 ID (필터)", example = "3")
        Integer positionId,

        @Schema(description = "조회 시작일", example = "2025-06-01")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,

        @Schema(description = "조회 종료일", example = "2025-06-30")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,

        @Schema(description = "페이지 번호", example = "1", defaultValue = "1")
        Integer page,

        @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
        Integer size
) {
    // 레코드에서 기본값을 설정할 수 없으므로, 생성자에서 기본값을 설정
    public RetentionContactListRequestDto {
        if (page == null) page = 1;
        if (size == null) size = 10;
    }
    public int getOffset() {
        return (page - 1) * size;
    }
}
