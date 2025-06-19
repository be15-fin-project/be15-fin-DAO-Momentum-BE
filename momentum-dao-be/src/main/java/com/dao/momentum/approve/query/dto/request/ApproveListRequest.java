package com.dao.momentum.approve.query.dto.request;

import com.dao.momentum.approve.command.domain.aggregate.ApproveType;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@Builder
public class ApproveListRequest {

    @NotNull(message = "결재 종류는 반드시 선택해야 합니다.")
    private final String tab;

    private final ApproveType approveType;
    private final String receiptType;
    private final Integer status;
    private final String title;
    private final String employeeName;
    private final String departmentName;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String sort;

}
