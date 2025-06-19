package com.dao.momentum.organization.employee.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class EmployeeListResponse {
    private List<EmployeeSummaryDTO> employees;

    private Pagination pagination;
}
