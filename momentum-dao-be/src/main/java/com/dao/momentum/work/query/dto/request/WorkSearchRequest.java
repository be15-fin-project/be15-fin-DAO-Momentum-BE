package com.dao.momentum.work.query.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class WorkSearchRequest {
    private LocalDate rangeStartDate;

    private LocalDate rangeEndDate;

}
