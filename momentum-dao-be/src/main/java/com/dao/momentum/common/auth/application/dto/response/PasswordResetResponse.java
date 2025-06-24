package com.dao.momentum.common.auth.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PasswordResetResponse {
    private String message;
}
