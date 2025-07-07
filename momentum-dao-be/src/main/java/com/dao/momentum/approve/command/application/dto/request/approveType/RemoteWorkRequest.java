package com.dao.momentum.approve.command.application.dto.request.approveType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@Builder
@Schema(description = "재택 근무 결재 request")
public class RemoteWorkRequest {

    @NotNull(message="재택 근무 시작 날짜는 반드시 작성해야 합니다.")
    @Schema(description = "재택 근무 시작 날짜")
    private final LocalDate startDate;

    @NotNull(message="재택 근무 종료 날짜는 반드시 작성해야 합니다.")
    @Schema(description = "재택 근무 종료 날짜")
    private final LocalDate endDate;

    @Schema(description = "재택 근무 사유")
    private final String reason;

}
