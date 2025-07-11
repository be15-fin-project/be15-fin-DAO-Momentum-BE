package com.dao.momentum.approve.command.application.dto.request.approveType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
@Schema(description = "초과 근무 결재 request")
public class OvertimeRequest {

    @NotNull(message="초과 근무 시작 시간은 반드시 작성해야 합니다.")
    @Schema(description = "초과 근무 시작 시간")
    private final LocalDateTime startAt;

    @NotNull(message="초과 근무 종료 시간은 반드시 작성해야 합니다.")
    @Schema(description = "초과 근무 종료 시간")
    private final LocalDateTime endAt;

    @Schema(description = "쉬는 시간")
    private final int breakTime;

    @NotNull(message="초과 근무 사유는 반드시 작성해야 합니다.")
    @Schema(description = "초과 근무 사유")
    private final String reason;

}
