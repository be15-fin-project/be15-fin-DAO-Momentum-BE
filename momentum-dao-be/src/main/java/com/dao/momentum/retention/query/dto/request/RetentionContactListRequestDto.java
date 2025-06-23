package com.dao.momentum.retention.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@Schema(description = "면담 기록 목록 조회 요청 DTO")
public class RetentionContactListRequestDto {

    @Schema(description = "사번", example = "20240001")
    private String targetNo;

    @Schema(description = "상급자 ID", example = "34")
    private Long managerId;

    @Schema(description = "상급자 사번", example = "34")
    private String managerNo;

    @Schema(description = "부서 ID (필터)", example = "5")
    private Integer deptId;

    @Schema(description = "직위 ID (필터)", example = "3")
    private Integer positionId;

    @Schema(description = "조회 시작일", example = "2025-06-01")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Schema(description = "조회 종료일", example = "2025-06-30")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @Schema(description = "페이지 번호", example = "1", defaultValue = "1")
    private int page = 1;

    @Schema(description = "페이지 크기", example = "10", defaultValue = "10")
    private int size = 10;

    public int getOffset() {
        return (page - 1) * size;
    }
}
