package com.dao.momentum.evaluation.eval.query.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class EvaluatorRoleDto {
    private Long empId;
    private Long deptId;
    private boolean isDeptHead;
    private boolean isTeamLeader;
}
