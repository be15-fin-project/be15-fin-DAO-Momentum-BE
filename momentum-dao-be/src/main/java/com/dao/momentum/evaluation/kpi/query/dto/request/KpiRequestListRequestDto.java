package com.dao.momentum.evaluation.kpi.query.dto.request;

import com.dao.momentum.common.dto.UseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "KPI 요청 목록 조회 요청 DTO")
public class KpiRequestListRequestDto {

    @Schema(description = "작성자 사번", example = "20240001")
    private String empNo;

    @Schema(description = "요청자 사원 ID", example = "20250001")
    private Long requesterEmpId;

    @Schema(description = "KPI 상태 ID", example = "2", allowableValues = {"1", "2", "3", "4", "5"})
    private Integer statusId;

    @Schema(description = "완료 여부 (true: 완료, false: 미완료)", example = "true")
    private Boolean completed;

    @Schema(description = "작성일 시작일자 (yyyy-MM-dd)", example = "2025-06-01")
    private String startDate;

    @Schema(description = "마감일 종료일자 (yyyy-MM-dd)", example = "2025-06-30")
    private String endDate;

    @Schema(description = "삭제 여부 (Y, N)", example = "Y")
    private UseStatus isDeleted;

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
