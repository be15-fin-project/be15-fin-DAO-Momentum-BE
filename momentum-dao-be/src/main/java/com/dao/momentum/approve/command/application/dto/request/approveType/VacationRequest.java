package com.dao.momentum.approve.command.application.dto.request.approveType;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@Builder
@Schema(description = "휴가 결재 request")
public class VacationRequest {

    @Schema(description = "휴가 타입", example = "1")
    private final int vacationTypeId;

    @NotNull(message="휴가 시작 날짜는 반드시 작성해야 합니다.")
    @Schema(description = "휴가 시작 날짜")
    private final LocalDate startDate;

    @NotNull(message="휴가 종료 날짜는 반드시 작성해야 합니다.")
    @Schema(description = "휴가 종료 날짜")
    private final LocalDate endDate;

    @Schema(description = "휴가 사유")
    private final String reason;

}
