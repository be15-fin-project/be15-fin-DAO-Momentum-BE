package com.dao.momentum.approve.command.application.dto.request;

import com.dao.momentum.approve.command.domain.aggregate.IsConfirmed;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@Builder
public class ApproveRefRequest {

    @NotBlank(message = "사원 id는 null일 수 없습니다.")
    private final Long empId;

    @NotNull
    private final IsConfirmed isConfirmed;

}
