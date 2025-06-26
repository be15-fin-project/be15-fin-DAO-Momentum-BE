package com.dao.momentum.organization.employee.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "사원 정보 상세 조회 응답 객체")
public class EmployeeDetailsResponse {
    @Schema(description = "사원 상세 정보")
    private EmployeeDTO employeeDetails;

    @Schema(description = "사원 인사 정보")
    private List<EmployeeRecordsDTO> employeeRecords;
}
