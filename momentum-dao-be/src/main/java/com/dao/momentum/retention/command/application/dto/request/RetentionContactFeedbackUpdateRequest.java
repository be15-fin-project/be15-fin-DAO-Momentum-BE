package com.dao.momentum.retention.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RetentionContactFeedbackUpdateRequest(

    @NotBlank(message = "피드백 내용은 필수입니다.")
    String feedback

) {}
