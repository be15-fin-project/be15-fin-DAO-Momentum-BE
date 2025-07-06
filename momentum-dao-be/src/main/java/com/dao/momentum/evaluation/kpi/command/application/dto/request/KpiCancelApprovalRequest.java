package com.dao.momentum.evaluation.kpi.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
@Schema(description = "KPI 취소 승인/반려 요청 DTO")
public record KpiCancelApprovalRequest(

        @NotNull
        @Schema(description = "KPI 취소 승인 여부 (true=승인, false=반려)", example = "true")
        Boolean approved,

        @Schema(description = "반려 사유 (반려일 경우 필수)", example = "취소 사유 불충분")
        String reason
) {

    public boolean isRejectedWithoutReason() {
        return approved != null && !approved && (reason == null || reason.trim().isEmpty());
    }

}
