package com.dao.momentum.approve.command.application.dto.request;

import com.dao.momentum.approve.command.domain.aggregate.IsRequiredAll;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
@Builder
public class ApproveLineRequest {

    @NotNull(message = "상태 아이디는 null일 수 없습니다.")
    private final Integer statusId;

    @NotNull(message = "결재선 순서는 null일 수 없습니다.")
    private final Integer approveLineOrder;

    private final IsRequiredAll isRequiredAll;

    private final List<ApproveLineListRequest> approveLineList;
}
