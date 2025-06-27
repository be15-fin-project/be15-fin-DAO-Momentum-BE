package com.dao.momentum.approve.command.application.dto.request.approveType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@RequiredArgsConstructor
@Builder
@Schema(description = "품의 결재 request")
public class ApproveProposalRequest {

    @NotBlank(message="품의 내용은 반드시 입력해야 합니다.")
    @Schema(description = "품의 결재 내용")
    private final String content;

}
