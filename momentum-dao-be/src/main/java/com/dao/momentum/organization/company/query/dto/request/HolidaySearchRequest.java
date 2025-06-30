package com.dao.momentum.organization.company.query.dto.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HolidaySearchRequest {
    private Integer year;

    private Integer month;

    private Integer page=1;

    private Integer size=10;
}
