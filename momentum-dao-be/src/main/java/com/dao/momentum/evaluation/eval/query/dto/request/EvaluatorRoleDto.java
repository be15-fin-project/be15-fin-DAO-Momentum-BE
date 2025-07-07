package com.dao.momentum.evaluation.eval.query.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "평가자 역할 DTO")
@Builder
public record EvaluatorRoleDto(

        @Schema(description = "사원 ID", example = "1001")
        Long empId,

        @Schema(description = "부서 ID", example = "10")
        Long deptId,

        @Schema(description = "부서장 여부", example = "true")
        boolean isDeptHead,

        @Schema(description = "팀장 여부", example = "false")
        boolean isTeamLeader
) { }
