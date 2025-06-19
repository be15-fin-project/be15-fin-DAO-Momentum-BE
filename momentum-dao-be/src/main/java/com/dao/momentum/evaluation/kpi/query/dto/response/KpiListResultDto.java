package com.dao.momentum.evaluation.kpi.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Schema(description = "KPI 목록 응답 DTO")
public class KpiListResultDto {

    @Schema(description = "KPI 목록 데이터")
    private List<KpiListResponseDto> content;

    @Schema(description = "페이지네이션 정보")
    private Pagination pagination;
}
