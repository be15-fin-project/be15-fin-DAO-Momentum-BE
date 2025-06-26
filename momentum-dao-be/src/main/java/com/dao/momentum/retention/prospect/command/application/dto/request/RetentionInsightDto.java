package com.dao.momentum.retention.prospect.command.application.dto.request;

public record RetentionInsightDto(
        Integer deptId,
        Integer positionId,
        int retentionScore,
        int empCount,
        int progress20,
        int progress40,
        int progress60,
        int progress80,
        int progress100
) {}
