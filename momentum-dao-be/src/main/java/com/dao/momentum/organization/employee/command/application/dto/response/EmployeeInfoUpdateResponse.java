package com.dao.momentum.organization.employee.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description="사원 기본 정보 수정 응답 객체")
public class EmployeeInfoUpdateResponse {
    @Schema(description = "수정된 사원 기본 정보")
    private EmployeeInfoDTO employeeInfo;

    @Schema(description = "응답 메시지", example = "사원 정보 수정 완료")
    private String message;
}
