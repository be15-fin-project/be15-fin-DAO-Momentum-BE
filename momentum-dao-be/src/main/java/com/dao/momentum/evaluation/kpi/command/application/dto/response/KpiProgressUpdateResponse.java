package com.dao.momentum.evaluation.kpi.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KpiProgressUpdateResponse {
    private Long kpiId;
    private Integer progress;
    private String message;
}
