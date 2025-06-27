package com.dao.momentum.organization.employee.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "사원 권한 응답 객체")
public class EmployeeRolesResponse {
    @Schema(description = "사용자 권한 ID 목록")
    private List<Integer> userRolesIds;
}
