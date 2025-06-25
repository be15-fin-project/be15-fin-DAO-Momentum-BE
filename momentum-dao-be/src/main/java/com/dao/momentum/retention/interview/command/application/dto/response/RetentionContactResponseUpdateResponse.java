package com.dao.momentum.retention.interview.command.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RetentionContactResponseUpdateResponse(
    Long retentionId,
    String response,
    LocalDateTime responseAt
) {}
