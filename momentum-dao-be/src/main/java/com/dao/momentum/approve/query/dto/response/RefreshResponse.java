package com.dao.momentum.approve.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "잔여 리프레시 휴가 응답 객체")
public class RefreshResponse {
    @Schema(description = "사원 ID", example = "1")
    private long empId;

    @Schema(description = "잔여 리프레시 휴가 일수", example = "2")
    private int remainingRefreshDays;
}
