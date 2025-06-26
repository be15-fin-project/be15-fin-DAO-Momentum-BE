package com.dao.momentum.organization.employee.command.application.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyInfoUpdateResponse {
    private Long empId;
    private String message;
}
