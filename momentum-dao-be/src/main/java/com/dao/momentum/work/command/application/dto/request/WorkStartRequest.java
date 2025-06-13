package com.dao.momentum.work.command.application.dto.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class WorkStartRequest {
    private LocalDateTime startPushedAt;
}
