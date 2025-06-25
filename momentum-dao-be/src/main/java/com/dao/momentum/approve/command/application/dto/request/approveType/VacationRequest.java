package com.dao.momentum.approve.command.application.dto.request.approveType;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@Builder
public class VacationRequest {

    private final int vacationTypeId;

    @NotNull(message="휴가 시작 날짜는 반드시 작성해야 합니다.")
    private final LocalDate startDate;

    @NotNull(message="휴가 종료 날짜는 반드시 작성해야 합니다.")
    private final LocalDate endDate;

    private final String reason;

}
