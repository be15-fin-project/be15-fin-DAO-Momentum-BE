package com.dao.momentum.retention.interview.command.application.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record RetentionContactDeleteDto(
    Long retentionId,
    Long loginEmpId
) {}
