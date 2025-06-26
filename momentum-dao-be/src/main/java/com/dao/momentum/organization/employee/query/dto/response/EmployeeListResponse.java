package com.dao.momentum.organization.employee.query.dto.response;

import com.dao.momentum.common.dto.Pagination;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "사원 목록 응답 객체")
public class EmployeeListResponse {
    @Schema(description ="사원 목록")
    private List<EmployeeSummaryDTO> employees;

    private Pagination pagination;
}
