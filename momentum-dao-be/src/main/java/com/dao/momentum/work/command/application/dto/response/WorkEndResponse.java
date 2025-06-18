package com.dao.momentum.work.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkEndResponse {
    private WorkSummaryDTO workSummaryDTO;

    private String message;
}
