package com.dao.momentum.common.auth.application.dto.response;

import com.dao.momentum.organization.employee.command.domain.aggregate.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String accessToken;
}
