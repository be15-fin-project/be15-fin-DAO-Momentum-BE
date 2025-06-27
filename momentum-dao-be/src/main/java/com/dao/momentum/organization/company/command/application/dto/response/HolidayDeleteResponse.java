package com.dao.momentum.organization.company.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HolidayDeleteResponse {
    private Integer holidayId;
    private String message;
}
