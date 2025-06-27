package com.dao.momentum.organization.company.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class HolidayGetResponse {
    private List<HolidayGetDTO> holidayGetDTOList;
    private Pagination pagination;
}
