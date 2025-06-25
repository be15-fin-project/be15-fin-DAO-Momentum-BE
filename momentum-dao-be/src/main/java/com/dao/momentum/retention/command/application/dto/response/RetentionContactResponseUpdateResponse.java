package com.dao.momentum.retention.command.application.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RetentionContactResponseUpdateResponse(
    Long retentionId,
    String response,
    LocalDateTime responseAt
) {}
