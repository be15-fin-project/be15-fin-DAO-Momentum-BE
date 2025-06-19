package com.dao.momentum.evaluation.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "KPI 사원별 요약 조회 요청 DTO")
public class KpiEmployeeSummaryRequestDto {

    @Schema(description = "조회 연도", example = "2025")
    private Integer year;

    @Schema(description = "조회 월", example = "6")
    private Integer month;

    @Schema(description = "부서 ID", example = "3")
    private Integer deptId;

    @Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
    private int page = 1;

    @Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
    private int size = 10;

    public int getOffset() {
        return (page - 1) * size;
    }
}
