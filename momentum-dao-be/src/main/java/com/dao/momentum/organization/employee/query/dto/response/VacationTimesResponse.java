package com.dao.momentum.organization.employee.query.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VacationTimesResponse {
    private VacationTimesDTO vacationTimes;
}