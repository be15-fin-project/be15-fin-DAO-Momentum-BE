package com.dao.momentum.retention.interview.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record RetentionContactResponseUpdateRequest(

    @NotBlank(message = "면담 보고 내용은 필수입니다.")
    String response

) {}
