package com.dao.momentum.work.command.application.dto.response;

import lombok.*;

@Getter
@Builder
public class WorkStartResponse {
    private WorkSummaryDTO workSummaryDTO;

    private String message;
}
