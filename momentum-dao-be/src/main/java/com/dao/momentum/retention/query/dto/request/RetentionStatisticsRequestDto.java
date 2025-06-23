package com.dao.momentum.retention.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "근속 전망 통계 조회 요청 DTO")
public class RetentionStatisticsRequestDto {

    @Schema(description = "조회 연도", example = "2025")
    private Integer year;

    @Schema(description = "조회 월", example = "6")
    private Integer month;

    @Schema(description = "부서 ID", example = "10")
    private Integer deptId;
}
