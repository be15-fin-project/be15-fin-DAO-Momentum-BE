package com.dao.momentum.approve.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
@Schema(description = "결재 승인/반려 request")
public class ApprovalConfirmRequest {

    @NotNull(message = "결재선 목록 아이디(결재자)는 null일 수 없습니다.")
    @Schema(description = "결재선 목록 ID", example = "1")
    private final Long approveLineListId;

    @NotNull(message = "승인/반려 여부는 반드시 선택해야 됩니다.")
    @Schema(description = "승인/반려 여부", example = "승인")
    private final String isApproved;

    @Schema(description = "승인/반려 사유", example = "내용이 충분하지 않아 반려합니다.")
    private final String reason;

}
