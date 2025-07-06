package com.dao.momentum.evaluation.kpi.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Builder
@Schema(description = "KPI 목록 응답 DTO")
public record KpiListResultDto(

        @Schema(description = "KPI 목록 데이터")
        List<KpiListResponseDto> content,

        @Schema(description = "페이지네이션 정보")
        Pagination pagination

) {}
