package com.dao.momentum.approve.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "잔여 연차 응답 객체")
public class DayoffResponse {
    @Schema(description = "사원 ID", example = "1")
    private long empId;

    @Schema(description = "잔여 연차 시간", example = "120")
    private int remainingDayoffHours;
}
