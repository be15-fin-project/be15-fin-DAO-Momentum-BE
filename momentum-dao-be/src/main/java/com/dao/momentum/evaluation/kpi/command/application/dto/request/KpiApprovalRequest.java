package com.dao.momentum.evaluation.kpi.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "KPI 승인/반려")
public record KpiApprovalRequest(

        @NotNull
        @Schema(description = "승인 여부 (true: 승인, false: 반려)", example = "true")
        Boolean approved,

        @Schema(description = "처리 사유 (반려 시 필수)", example = "목표가 불명확합니다.")
        String reason
) {

    public boolean isRejectedWithoutReason() {
        return Boolean.FALSE.equals(approved) && (reason == null || reason.trim().isEmpty());
    }

}
