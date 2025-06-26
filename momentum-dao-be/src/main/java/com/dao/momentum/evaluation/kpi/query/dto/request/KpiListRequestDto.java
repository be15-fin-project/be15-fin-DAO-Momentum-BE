package com.dao.momentum.evaluation.kpi.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "KPI 목록 조회 요청 DTO")
public class KpiListRequestDto {

    @Schema(description = "사번", example = "20240001")
    private String empNo;

    @Schema(description = "부서 ID", example = "10")
    private Integer deptId;

    @Schema(description = "직위 ID", example = "5")
    private Integer positionId;

    @Schema(
            description = "KPI 상태 ID",
            example = "2",
            allowableValues = {"1", "2", "3", "4", "5"}
    )
    private Integer statusId;

    @Schema(description = "작성일 시작일자 (yyyy-MM-dd)", example = "2025-06-01")
    private String startDate;

    @Schema(description = "작성일 종료일자 (yyyy-MM-dd)", example = "2025-06-30")
    private String endDate;

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
