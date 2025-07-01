package com.dao.momentum.evaluation.kpi.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "KPI 사원별 요약 조회 요청 DTO")
public class KpiEmployeeSummaryRequestDto {

    @Schema(description = "사번", example = "20240001")
    private String empNo;

    @Schema(description = "조회 연도", example = "2025")
    private Integer year;

    @Schema(description = "조회 월", example = "6")
    private Integer month;

    @Schema(description = "부서 ID", example = "3")
    private Integer deptId;

    @Builder.Default
    @Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
    private Integer page = 1;

    @Builder.Default
    @Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
    private Integer size = 10;

    public int getOffset() {
        return (page - 1) * size;
    }
}
