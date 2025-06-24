package com.dao.momentum.evaluation.kpi.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "KPI 생성 응답")
public class KpiCreateResponse {

    @Schema(description = "생성된 KPI ID", example = "101")
    private Long kpiId;

    @Schema(description = "처리 결과 메시지", example = "KPI가 성공적으로 생성되었습니다.")
    private String message;
}
