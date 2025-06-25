package com.dao.momentum.retention.command.application.dto.request;

import lombok.Builder;

@Builder
public record RetentionContactResponseUpdateDto(
    Long retentionId,
    Long loginEmpId,
    String response
) {}
