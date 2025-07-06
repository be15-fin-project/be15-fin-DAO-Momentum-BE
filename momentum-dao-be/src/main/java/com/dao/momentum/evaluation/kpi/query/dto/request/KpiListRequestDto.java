package com.dao.momentum.evaluation.kpi.query.dto.request;

import com.dao.momentum.common.dto.UseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "KPI 목록 조회 요청 DTO")
public record KpiListRequestDto(

        @Schema(description = "사번", example = "20240001")
        String empNo,

        @Schema(description = "부서 ID", example = "10")
        Integer deptId,

        @Schema(description = "직위 ID", example = "5")
        Integer positionId,

        @Schema(
                description = "KPI 상태 ID",
                example = "2",
                allowableValues = {"1", "2", "3", "4", "5"}
        )
        Integer statusId,

        @Schema(description = "작성일 시작일자 (yyyy-MM-dd)", example = "2025-06-01")
        String startDate,

        @Schema(description = "작성일 종료일자 (yyyy-MM-dd)", example = "2025-06-30")
        String endDate,

        @Schema(description = "삭제 여부 (Y, N)", example = "Y")
        UseStatus isDeleted,

        @Schema(description = "페이지 번호 (1부터 시작)", example = "1", defaultValue = "1")
        Integer page,

        @Schema(description = "페이지당 항목 수", example = "10", defaultValue = "10")
        Integer size

) {

    // 레코드는 생성자에서 기본값을 설정할 수 없으므로, 기본값을 설정하는 로직은 생성자에서 처리해야 합니다.
    public KpiListRequestDto {
        if (page == null) page = 1;
        if (size == null) size = 10;
    }

    public int getOffset() {
        return (page - 1) * size;
    }

    @Override
    public String toString() {
        return "KpiListRequestDto{" +
                "empNo='" + empNo + '\'' +
                ", deptId=" + deptId +
                ", positionId=" + positionId +
                ", statusId=" + statusId +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", page=" + page +
                ", size=" + size +
                '}';
    }

}
