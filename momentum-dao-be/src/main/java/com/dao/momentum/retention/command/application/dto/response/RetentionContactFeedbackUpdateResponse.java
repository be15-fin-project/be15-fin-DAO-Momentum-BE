package com.dao.momentum.retention.command.application.dto.response;

import lombok.Builder;

@Builder
public record RetentionContactFeedbackUpdateResponse(
    Long retentionId,
    String feedback
) {}
