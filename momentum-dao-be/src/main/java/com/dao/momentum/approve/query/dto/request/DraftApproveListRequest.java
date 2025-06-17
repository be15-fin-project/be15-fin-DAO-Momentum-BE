package com.dao.momentum.approve.query.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
@Builder
public class DraftApproveListRequest {

    @NotNull(message = "결재 종류는 반드시 선택해야 합니다.")
    private final String tab;

    private final String approveType;
    private final String receiptType;
    private final Integer status;
    private final String title;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String sort;

}
