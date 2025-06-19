package com.dao.momentum.evaluation.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "KPI 시계열 조회 요청 DTO")
public class KpiTimeseriesRequestDto {

    @Schema(
            description = "조회 연도 (nullable, 미입력 시 현재 연도 사용)",
            example = "2025"
    )
    private Integer year;
}
