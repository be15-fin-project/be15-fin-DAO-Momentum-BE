package com.dao.momentum.organization.employee.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "사원 권한 DTO")
public class EmployeeRoleDTO {
    @Schema(description = "사용자 권한 ID", example = "1")
    private int userRoleId;
}
