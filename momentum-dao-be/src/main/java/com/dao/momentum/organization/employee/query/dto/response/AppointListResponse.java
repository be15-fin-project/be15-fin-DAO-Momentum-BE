package com.dao.momentum.organization.employee.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AppointListResponse {
    private List<AppointDTO> appoints;

    private Pagination pagination;
}
