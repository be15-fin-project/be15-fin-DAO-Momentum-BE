package com.dao.momentum.organization.employee.query.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "사용자 권한 DTO")
public class UserRoleDTO {
    @Schema(description = "권한 ID", example = "4")
    private int userRoleId;

    @Schema(description = "권한 이름", example = "MANAGER")
    private String userRoleName;
}
