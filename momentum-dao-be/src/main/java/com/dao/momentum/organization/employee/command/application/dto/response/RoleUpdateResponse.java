package com.dao.momentum.organization.employee.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "권한 수정 응답 객체")
public class RoleUpdateResponse {
    @Schema(description = "사원 권한 ID 목록 (다대다 매핑)")
    List<Long> employeeRolesIds;

    @Schema(description = "사용자 권한 ID 목록")
    List<Integer> userRolesIds;

    @Schema(description = "응답 메시지", example = "사원 권한 수정 완료")
    String message;
}
