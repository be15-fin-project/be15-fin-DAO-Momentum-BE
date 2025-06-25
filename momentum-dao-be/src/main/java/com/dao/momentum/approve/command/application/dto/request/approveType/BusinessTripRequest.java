package com.dao.momentum.approve.command.application.dto.request.approveType;

import com.dao.momentum.work.command.domain.aggregate.TypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@Builder
public class BusinessTripRequest {

    @NotNull(message="출장 종류는 반드시 선택해야 합니다.")
    private final TypeEnum type;

    @NotBlank(message="출장 장소는 반드시 작성해야 합니다.")
    private final String place;

    @NotNull(message="출장 시작 날짜는 반드시 작성해야 합니다.")
    private final LocalDate startDate;

    @NotNull(message="출장 종료 날짜는 반드시 작성해야 합니다.")
    private final LocalDate endDate;

    @NotNull(message="출장 사유는 반드시 작성해야 합니다.")
    private final String reason;

    private final int cost;

}
