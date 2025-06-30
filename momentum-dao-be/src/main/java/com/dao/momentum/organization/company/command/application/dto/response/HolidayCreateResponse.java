package com.dao.momentum.organization.company.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HolidayCreateResponse {
    private Integer holidayId;
    private String message;
}
