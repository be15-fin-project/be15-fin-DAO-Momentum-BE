package com.dao.momentum.approve.command.application.dto.request.approveType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@RequiredArgsConstructor
@Builder
@Schema(description = "취소 결재 request")
public class ApproveCancelRequest {

    @NotBlank(message="취소 사유는 반드시 입력해야 합니다.")
    @Schema(description = "취소 사유")
    private final String cancelReason;

}
