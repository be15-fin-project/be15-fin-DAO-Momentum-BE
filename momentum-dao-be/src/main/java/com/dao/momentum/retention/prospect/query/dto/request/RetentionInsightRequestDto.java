package com.dao.momentum.retention.prospect.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "근속 통계 조회 요청 DTO")
public class RetentionInsightRequestDto {

    @Schema(description = "회차 ID", example = "3")
    private Integer roundId;

    @Schema(description = "부서 ID", example = "10")
    private Integer deptId;

    @Schema(description = "직급 ID", example = "5")
    private Integer positionId;
}
