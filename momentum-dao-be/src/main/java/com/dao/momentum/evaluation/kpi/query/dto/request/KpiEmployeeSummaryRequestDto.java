package com.dao.momentum.evaluation.kpi.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "KPI 사원별 요약 조회 요청 DTO")
public record KpiEmployeeSummaryRequestDto(

        @Schema(description = "사번", example = "20240001")
        String empNo,

        @Schema(description = "조회 연도", example = "2025")
        Integer year,

        @Schema(description = "조회 월", example = "6")
        Integer month,

        @Schema(description = "부서 ID", example = "3")
        Integer deptId,

        @Schema(description = "직위 ID", example = "5")
        Integer positionId,

        @Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
        Integer page,

        @Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
        Integer size

) {

    // 레코드는 생성자에서 기본값을 설정할 수 없습니다. 대신, 기본값을 필드에 설정합니다.
    public KpiEmployeeSummaryRequestDto {
        // 기본값 설정
        if (page == null) page = 1;
        if (size == null) size = 10;
    }

    public int getOffset() {
        return (page - 1) * size;
    }
}
