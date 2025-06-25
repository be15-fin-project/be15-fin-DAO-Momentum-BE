package com.dao.momentum.retention.interview.command.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateRetentionContactRequest(

    @NotNull(message = "면담 대상자 ID는 필수입니다.")
    Long targetId,

    @NotNull(message = "상급자 ID는 필수입니다.")
    Long managerId,

    @NotBlank(message = "면담 사유는 필수입니다.")
    String reason

) {}
