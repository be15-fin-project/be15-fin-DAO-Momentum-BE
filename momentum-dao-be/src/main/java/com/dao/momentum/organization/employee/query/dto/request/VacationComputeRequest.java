package com.dao.momentum.organization.employee.query.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VacationComputeRequest {
    private int joinYear;

    private int joinMonth;

    private Integer targetYear;
}
