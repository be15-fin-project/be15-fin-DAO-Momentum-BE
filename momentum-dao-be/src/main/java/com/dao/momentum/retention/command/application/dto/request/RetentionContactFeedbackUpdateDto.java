package com.dao.momentum.retention.command.application.dto.request;

import lombok.Builder;

import java.util.List;

@Builder
public record RetentionContactFeedbackUpdateDto(
    Long retentionId,
    Long loginEmpId,
    String feedback
) {}
