package com.dao.momentum.retention.prospect.command.application.dto.request;

import java.math.BigDecimal;

public record RetentionSupportDto(
        int jobLevel,
        int compLevel,
        int relationLevel,
        int growthLevel,
        BigDecimal tenureLevel,
        int wlbLevel,
        int retentionScore
) {}
