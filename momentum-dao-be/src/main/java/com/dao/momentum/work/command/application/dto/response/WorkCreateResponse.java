package com.dao.momentum.work.command.application.dto.response;

import lombok.Builder;

@Builder
public class WorkCreateResponse {
    private WorkSummaryDTO workSummaryDTO;

    private String message;
}
