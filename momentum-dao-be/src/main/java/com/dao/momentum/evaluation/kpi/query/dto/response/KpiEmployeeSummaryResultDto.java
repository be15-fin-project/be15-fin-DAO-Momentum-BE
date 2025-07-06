package com.dao.momentum.evaluation.kpi.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "KPI 사원별 요약 조회 결과 DTO")
public record KpiEmployeeSummaryResultDto(

        @Schema(description = "사원별 KPI 요약 목록")
        List<KpiEmployeeSummaryResponseDto> content,

        @Schema(description = "페이지네이션 정보")
        Pagination pagination

) {}
