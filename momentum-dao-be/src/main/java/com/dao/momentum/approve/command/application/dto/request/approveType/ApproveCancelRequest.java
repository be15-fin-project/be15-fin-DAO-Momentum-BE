package com.dao.momentum.approve.command.application.dto.request.approveType;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@RequiredArgsConstructor
@Builder
public class ApproveCancelRequest {

    @NotBlank(message="취소 사유는 반드시 입력해야 합니다.")
    private final String cancelReason;

}
