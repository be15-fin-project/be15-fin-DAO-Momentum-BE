package com.dao.momentum.retention.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "부서별 안정성 비율 원시 DTO")
public class StabilityRatioByDeptRaw {

    @Schema(description = "부서명", example = "기획팀")
    private String deptName;

    @Schema(description = "안정형 인원 수", example = "15")
    private Long stableCount;

    @Schema(description = "경고형 인원 수", example = "5")
    private Long warningCount;

    @Schema(description = "불안정형 인원 수", example = "3")
    private Long unstableCount;

    @Schema(description = "총 인원 수", example = "23")
    private Long totalCount;
}
