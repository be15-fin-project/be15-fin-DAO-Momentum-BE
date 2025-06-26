package com.dao.momentum.retention.prospect.command.application.dto.response;

import lombok.Builder;

public record RetentionForecastCreateResponse(
        Integer roundId,
        String message
) {
    @Builder
    public RetentionForecastCreateResponse {}
}
