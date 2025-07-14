package com.dao.momentum.organization.company.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class HolidayGetDTO {
    private Integer holidayId;
    private String holidayName;
    private LocalDate date;
}
