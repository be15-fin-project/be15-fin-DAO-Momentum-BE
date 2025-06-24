package com.dao.momentum.evaluation.kpi.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "KPI 승인/반려 응답")
public class KpiApprovalResponse {

    @Schema(description = "KPI ID", example = "101")
    private Long kpiId;

    @Schema(description = "변경된 상태", example = "ACCEPTED")
    private String status;

    @Schema(description = "처리 메시지", example = "KPI가 승인되었습니다.")
    private String message;
}
