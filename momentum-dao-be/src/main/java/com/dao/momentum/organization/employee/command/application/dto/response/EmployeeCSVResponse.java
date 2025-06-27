package com.dao.momentum.organization.employee.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "사원 CSV 일괄 등록 응답 객체")
public class EmployeeCSVResponse {
    @Schema(description = "등록된 사원 ID 목록")
    private List<Long> empIds;

    @Schema(description = "응답 메시지", example = "사원 CSV 등록 완료")
    private String message;
}
