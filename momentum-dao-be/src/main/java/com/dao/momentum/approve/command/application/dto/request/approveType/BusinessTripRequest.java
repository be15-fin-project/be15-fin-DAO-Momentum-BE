package com.dao.momentum.approve.command.application.dto.request.approveType;

import com.dao.momentum.work.command.domain.aggregate.TypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@Builder
@Schema(description = "출장 결재 request")
public class BusinessTripRequest {

    @NotNull(message="출장 종류는 반드시 선택해야 합니다.")
    @Schema(description = "출장 종류")
    private final TypeEnum type;

    @NotBlank(message="출장 장소는 반드시 작성해야 합니다.")
    @Schema(description = "출장 장소")
    private final String place;

    @NotNull(message="출장 시작 날짜는 반드시 작성해야 합니다.")
    @Schema(description = "출장 시작 날짜")
    private final LocalDate startDate;

    @NotNull(message="출장 종료 날짜는 반드시 작성해야 합니다.")
    @Schema(description = "출장 종료 날짜")
    private final LocalDate endDate;

    @NotNull(message="출장 사유는 반드시 작성해야 합니다.")
    @Schema(description = "출장 사유")
    private final String reason;

    @Schema(description = "출장 비용")
    private final int cost;

}
