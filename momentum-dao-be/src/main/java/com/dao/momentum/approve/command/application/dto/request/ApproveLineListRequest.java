package com.dao.momentum.approve.command.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
@Schema(description = "결재자 지정 request")
public class ApproveLineListRequest {

    @NotNull(message = "상태 아이디는 null일 수 없습니다.")
    @Schema(description = "결재선 목록 상태(대기, 승인, 반려) 아이디", example = "1")
    private final Integer statusId;

    @NotNull(message = "사원 아이디는 null일 수 없습니다.")
    @Schema(description = "결재선에 지정된 사원 ID", example = "1")
    private final Long empId;

}
