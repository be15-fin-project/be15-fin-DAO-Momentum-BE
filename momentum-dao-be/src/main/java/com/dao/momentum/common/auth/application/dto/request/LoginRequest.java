package com.dao.momentum.common.auth.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class LoginRequest {
    @NotBlank
    private final String email;

    @NotBlank
    private final String password;
}
