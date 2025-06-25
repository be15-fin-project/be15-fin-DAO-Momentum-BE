package com.dao.momentum.approve.command.application.dto.request.approveType;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@Builder
public class OvertimeRequest {

    @NotNull(message="초과 근무 시작 시간은 반드시 작성해야 합니다.")
    private final LocalDateTime startAt;

    @NotNull(message="초과 근무 종료 시간은 반드시 작성해야 합니다.")
    private final LocalDateTime endAt;

    private final int breakTime;

    @NotNull(message="출장 이유는 반드시 작성해야 합니다.")
    private final String reason;

}
