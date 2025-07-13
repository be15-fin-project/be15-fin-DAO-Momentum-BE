package com.dao.momentum.retention.interview.command.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "면담 대상자와 상급자의 부서 정보 응답")
@Builder
public record RetentionContactManagerInfoResponse(

        @Schema(description = "면담 대상자 ID", example = "42")
        Long targetId,

        @Schema(description = "면담 대상자의 부서 ID", example = "10")
        Integer targetDeptId,

        @Schema(description = "상급자(면담자) ID", example = "13")
        Long managerId,

        @Schema(description = "상급자의 부서 ID", example = "5")
        Integer managerDeptId

) {}
