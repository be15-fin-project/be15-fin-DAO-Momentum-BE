package com.dao.momentum.evaluation.kpi.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "KPI 승인/반려")
public class KpiApprovalRequest {

    @NotNull
    @Schema(description = "승인 여부 (true: 승인, false: 반려)", example = "true")
    private Boolean approved;

    @Schema(description = "처리 사유 (반려 시 필수)", example = "목표가 불명확합니다.")
    private String reason;

    public boolean isRejectedWithoutReason() {
        return Boolean.FALSE.equals(approved) && (reason == null || reason.trim().isEmpty());
    }

    public KpiApprovalRequest(Boolean approved, String reason) {
        this.approved = approved;
        this.reason = reason;
    }

}
