package com.dao.momentum.approve.command.application.dto.request.approveType;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@RequiredArgsConstructor
@Builder
public class ApproveProposalRequest {

    @NotBlank(message="품의 내용은 반드시 입력해야 합니다.")
    private final String content;

}
