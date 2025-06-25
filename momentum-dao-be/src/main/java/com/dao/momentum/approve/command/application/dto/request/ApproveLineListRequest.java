package com.dao.momentum.approve.command.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class ApproveLineListRequest {

    @NotNull(message = "상태 아이디는 null일 수 없습니다.")
    private final Integer statusId;

    @NotNull(message = "사원 아이디는 null일 수 없습니다.")
    private final Long empId;

}
