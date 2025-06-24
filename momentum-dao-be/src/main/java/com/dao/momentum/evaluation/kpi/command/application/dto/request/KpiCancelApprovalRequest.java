package com.dao.momentum.evaluation.kpi.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "KPI 취소 승인/반려 요청 DTO")
public class KpiCancelApprovalRequest {

    @NotNull
    @Schema(description = "KPI 취소 승인 여부 (true=승인, false=반려)", example = "true")
    private Boolean approved;

    @Schema(description = "반려 사유 (반려일 경우 필수)", example = "취소 사유 불충분")
    private String reason;

    public boolean isRejectedWithoutReason() {
        return approved != null && !approved && (reason == null || reason.trim().isEmpty());
    }

    public KpiCancelApprovalRequest(Boolean approved, String reason) {
        this.approved = approved;
        this.reason = reason;
    }

}
