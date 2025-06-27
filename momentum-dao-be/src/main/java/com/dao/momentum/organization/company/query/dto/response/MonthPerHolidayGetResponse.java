package com.dao.momentum.organization.company.query.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MonthPerHolidayGetResponse {
    private List<HolidayGetDTO> holidayGetDTOList;
}
